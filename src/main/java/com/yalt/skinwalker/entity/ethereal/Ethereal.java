package com.yalt.skinwalker.entity.ethereal;

import com.yalt.skinwalker.entity.ethereal.ai.*;
import com.yalt.skinwalker.entity.walker.SkinWalkerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ethereal extends Cow {
    private static final EntityDataAccessor<Integer> BUDGET = SynchedEntityData.defineId(Ethereal.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> REGEN = SynchedEntityData.defineId(Ethereal.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RADIUS = SynchedEntityData.defineId(Ethereal.class,
            EntityDataSerializers.INT);
    private static final int BUDGET_UPDATE_INTERVAL = 600;
    private static final double MAX_HEALTH = 100D;
    private static final float ATTACK_DAMAGE = 8.0f;
    private static final float ATTACK_SPEED = 4.0f;
    private static final double KNOCKBACK_RESISTANCE = 0.8;
    private static final float MOVEMENT_SPEED = 0.4f;
    private static final int ACQUIRE_RANGE = 1000;
    private static final int BUDGET_OFFSET = 1;
    private static final int BUDGET_MAXIUM = 10;
    private static final int BUDGET_MINIMUM = 0;
    private static final double MINIMUM_HEALTH = 1.0;
    private Player fakePlayer;
    // Instance variables
    private int budgetUpdateCounter = 0;
    public Player targetPlayer;
    public Mob possessedEntity;
    public boolean hasPossessedEntity;
    public boolean updatedPossessedEntityGoals;
    private Goal possessedGoal;

    private final Set<Goal> goalsToAddQueue = new HashSet<>();
    private final Set<Goal> goalsToRemoveQueue = new HashSet<>();

    Logger logger = Logger.getLogger(getClass().getName());

    public Ethereal(EntityType<? extends Ethereal> entityType, Level level) {
        super(entityType, level);
    }

    public static boolean canSpawn(EntityType<Ethereal> entityType, LevelAccessor level, MobSpawnType spawnType,
                                   BlockPos position, RandomSource random) {
        // Check if there's already an entity of this type in the world
        List<Ethereal> existingEntities = level.getEntitiesOfClass(Ethereal.class, new AABB(position).inflate(1000.0f));
        if (!existingEntities.isEmpty()) {
            return false;
        }

        // Allow the entity to spawn
        return true;
    }

    @Override
    public boolean shouldDespawnInPeaceful() {
        // Get the nearest player
        Player nearestPlayer = this.level().getNearestPlayer(this, 1000.0f);
        if (nearestPlayer == null) {
            return true;
        }

        // Check the distance to the nearest player
        double distanceToPlayer = this.distanceTo(nearestPlayer);
        if (distanceToPlayer > 100.0f) { // Replace 100.0f with the maximum distance you want
            return true;
        }

        // Don't despawn if the player is close enough
        return false;
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.ATTACK_SPEED, ATTACK_SPEED)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StayNearPlayerGoal(this));
        this.goalSelector.addGoal(2, new AggroGoal(this));
        this.goalSelector.addGoal(2, new PossessGoal(this));
        this.goalSelector.addGoal(3, new CurseGoal(this));
        this.goalSelector.addGoal(4, new SabotageGoal(this));
        this.goalSelector.addGoal(5, new InvokeGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BUDGET, 10);
        this.entityData.define(REGEN, 1);
        this.entityData.define(RADIUS, 25);
    }

    @Override
    public void tick() {
        super.tick();

        this.checkForOtherEntities();
        this.tryToAcquireTarget();
        this.updateGoalsIfNeeded();
        this.handleBudgetUpdate();

        // If Ethereal has possessed an entity, teleport to that entity's position
        if (hasPossessedEntity && possessedEntity != null) {
            Vec3 position = this.position();
            possessedEntity.teleportTo(position.x, position.y, position.z);
        }

        this.budgetUpdateCounter++;

    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        System.out.println("Entity was hurt");
        boolean result = super.hurt(source, amount);
        AggroGoal aggroGoal = new AggroGoal(this);
        aggroGoal.start();

        return result;
    }

    private void checkForOtherEntities() {
        List<Entity> entities = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(100));
        for (Entity entity : entities) {
            if (entity instanceof Ethereal || entity instanceof SkinWalkerEntity) {
                if (!entity.equals(this)) {
                    this.remove(Entity.RemovalReason.DISCARDED);
                    break;
                }
            }
        }
    }

    private void handleBudgetUpdate() {
        if (++budgetUpdateCounter >= BUDGET_UPDATE_INTERVAL) {
            updateBudget(BUDGET_OFFSET);
            budgetUpdateCounter = 0;
        }
    }

    public void updateBudget(int offset) {

        int budget = this.entityData.get(BUDGET) + offset;

        if (budget < BUDGET_MINIMUM || budget >= BUDGET_MAXIUM)
            return;

        System.out.println("Updating Budget to" + budget);
        this.entityData.set(BUDGET, budget);
        return;
    }

    @Nullable
    public Animal getNearestAnimal() {
        var range = this.entityData.get(RADIUS);
        var box = this.getBoundingBox().inflate(range);
        var entities = this.level().getEntitiesOfClass(Animal.class, box);

        var self = this.getUUID();
        return entities.stream()
                .filter(e -> !e.getUUID().equals(self)) // Exclude self
                .filter(e -> e.getHealth() > MINIMUM_HEALTH)
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElse(null);
    }

    public boolean hasBudget() {
        return this.entityData.get(BUDGET) != BUDGET_MINIMUM;
    }

    public void tryToAcquireTarget() {
        if (this.targetPlayer != null && this.targetPlayer.distanceToSqr(this) < ACQUIRE_RANGE) {
            return;
        }

        var cond = TargetingConditions.forNonCombat().range(ACQUIRE_RANGE);
        this.targetPlayer = level().getNearestPlayer(cond, this);
    }

    @Nullable
    public Player getTarget() {
        return this.targetPlayer;
    }

    @Nullable
    public boolean hasTarget() {
        // System.out.println("target? " + (target != null));
        return this.targetPlayer != null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean isColliding(BlockPos pos, BlockState state) {
        // Ignore block collisions
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return null;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return null;
    }

    protected SoundEvent getDeathSound() {
        return null;
    }

    public int getRadius() {
        return this.entityData.get(RADIUS);
    }

    @Nullable
    public Stream<LevelChunk> getChunks() {
        var player = getTarget();
        var level = player.level();
        var block = player.chunkPosition().getWorldPosition();
        System.out.println("chunk center: " + block.toString());
        return Stream.of(
                level.getChunkAt(block),
                level.getChunkAt(block.north(16)),
                level.getChunkAt(block.east(16)),
                level.getChunkAt(block.south(16)),
                level.getChunkAt(block.west(16)));
    }

    @Nullable
    public Stream<BlockEntity> getBlocks() {
        var chunks = this.getChunks();
        if (chunks == null) {
            return null;
        }

        return chunks.flatMap(chunk -> chunk.getBlockEntities().values().stream());
    }

    public synchronized void possess(Mob target) {
        if (target != null) {
            this.setInvisible(false);
            possessedEntity = target;
            hasPossessedEntity = true;
            updatedPossessedEntityGoals = false;
            this.queueGoalsToUpdate();

            // Remove other goals from the possessed entity
            if (possessedEntity.goalSelector != null) {
                List<Goal> goalsToRemove = possessedEntity.goalSelector.getRunningGoals()
                        .filter(goal -> !(goal.getGoal() instanceof PossessedGoal))
                        .map(WrappedGoal::getGoal)
                        .collect(Collectors.toList());

                for (Goal goal : goalsToRemove) {
                    possessedEntity.goalSelector.removeGoal(goal);
                }
            }

            // Remove the fake player if it exists
            if (this.fakePlayer != null) {
                this.fakePlayer.remove(Entity.RemovalReason.DISCARDED);
                this.fakePlayer = null;
            }
        }
    }

    public synchronized void unpossess() {
        if (possessedEntity != null) {
            this.setInvisible(false);
            possessedEntity = null;
            hasPossessedEntity = false;
            updatedPossessedEntityGoals = false;
            this.queueGoalsToUpdate();
        }
    }

    private boolean shouldAddPossessedGoal() {
        // Add PossessedGoal if the entity was newly possessed and goals have not been
        // updated
        return hasPossessedEntity && !updatedPossessedEntityGoals;
    }

    private boolean shouldRemovePossessedGoal() {
        // Remove PossessedGoal if the entity is being unpossessed
        return !hasPossessedEntity && updatedPossessedEntityGoals;
    }

    public synchronized void updateGoalsIfNeeded() {
        Logger logger = Logger.getLogger(getClass().getName());

        if (!goalsToAddQueue.isEmpty() || !goalsToRemoveQueue.isEmpty()) {
            logger.info("Updating entity goals");
            if (this.possessedEntity != null && !updatedPossessedEntityGoals) {
                List<Goal> currentGoals = new ArrayList<>(this.possessedEntity.goalSelector.getAvailableGoals().stream()
                        .map(WrappedGoal::getGoal).collect(Collectors.toList()));

                // Add goals
                for (Goal goal : goalsToAddQueue) {
                    if (!currentGoals.contains(goal)) {
                        this.possessedEntity.goalSelector.addGoal(0, goal);
                        logger.info("Added goal: " + goal);
                    }
                }

                // Remove goals
                for (Goal goal : goalsToRemoveQueue) {
                    if (currentGoals.contains(goal)) {
                        this.possessedEntity.goalSelector.removeGoal(goal);
                        logger.info("Removed goal: " + goal);
                    }
                }

                goalsToAddQueue.clear();
                goalsToRemoveQueue.clear();
                updatedPossessedEntityGoals = true;
            } else {
                logger.warning("Possessed entity is not an instance of Mob. Goals were not updated.");
            }
        }
    }

    public void playEntitySound(SoundEvent soundEvent, float volume, float pitch) {
        this.playSound(soundEvent, volume, pitch);
    }

    // public void disguiseAsPlayer() {
    //     Player targetPlayer = this.getTarget();
    //     if (targetPlayer != null) {
    //         GameProfile gameProfile = new GameProfile(UUID.randomUUID(), targetPlayer.getName().getString());

    //         // Delay the execution of the disguiseAsPlayer method
    //         this.level().getServer().execute(() -> {
    //             // Remove the fake player if it exists
    //             if (this.fakePlayer != null) {
    //                 this.fakePlayer.remove(Entity.RemovalReason.DISCARDED);
    //                 this.fakePlayer = null;
    //             }

    //             this.fakePlayer = FakePlayerFactory.get((ServerLevel) this.level(), gameProfile);
    //             this.fakePlayer.copyPosition(this);

    //             // Add player info to the server before adding the entity
    //             // PlayerInfoPacket cannot be resolved to a type, so we need to import it or use the fully qualified name
    //             net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket addPlayerPacket = new net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket(net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket.Action.ADD_PLAYER, this.fakePlayer);
    //             ((ServerLevel) this.level()).getServer().getPlayerList().broadcastAll(addPlayerPacket);

    //             this.level().addFreshEntity(this.fakePlayer);
    //         });
    //     }
    // }

    // public Player getFakePlayer() {
    //     return this.fakePlayer;
    // }

    public synchronized void queueGoalsToUpdate() {
        if (shouldAddPossessedGoal()) {
            if (this.possessedGoal == null) {
                this.possessedGoal = new PossessedGoal(this.possessedEntity, 0.4f, 15.0f);
            }
            goalsToAddQueue.add(this.possessedGoal);
        } else if (shouldRemovePossessedGoal()) {
            goalsToRemoveQueue.add(this.possessedGoal);
        }
    }

    public boolean hasBetterTarget() {
        Animal nearestAnimal = getNearestAnimal();
        if (nearestAnimal != null && possessedEntity != null) {
            return this.distanceToSqr(nearestAnimal) < this.distanceToSqr(possessedEntity);
        }
        return false;
    }

    public Entity getPossessedEntity() {
        return this.possessedEntity;
    }
}