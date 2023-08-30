package com.yalt.skinwalker.entity.ethereal;

import com.yalt.skinwalker.entity.ethereal.ai.*;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ethereal extends Cow {
    private static final EntityDataAccessor<Integer> BUDGET = SynchedEntityData.defineId(Ethereal.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> REGEN = SynchedEntityData.defineId(Ethereal.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RADIUS = SynchedEntityData.defineId(Ethereal.class, EntityDataSerializers.INT);
    private static final int BUDGET_UPDATE_INTERVAL = 600;


    // Instance variables
    private int budgetUpdateCounter = 0;
    public Player target;
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

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100D)
                .add(Attributes.ATTACK_DAMAGE, 8.0f)
                .add(Attributes.ATTACK_SPEED, 4.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.MOVEMENT_SPEED, 0.4f).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AggroGoal(this));
        this.goalSelector.addGoal(2, new PossessGoal(this));
        this.goalSelector.addGoal(3, new InvokeGoal(this));
        this.goalSelector.addGoal(3, new SabotageGoal(this));
        this.goalSelector.addGoal(3, new CurseGoal(this));
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

        this.tryToAcquireTarget();
        this.updateGoalsIfNeeded();
        this.handleBudgetUpdate();

        // If Ethereal has possessed an entity, teleport to that entity's position
        if (hasPossessedEntity && possessedEntity != null) {
            Vec3 position = possessedEntity.position();
            this.teleportTo(position.x, position.y, position.z);
        }

//        this.budgetUpdateCounter++;
//
//        // If 30 seconds have passed (600 ticks), update the budget and reset the counter
//        if (this.budgetUpdateCounter >= 600) {
//            updateBudget(1); // Add 1 to the budget
//            this.budgetUpdateCounter = 0; // Reset the counter
//        }
    }

    private void handleBudgetUpdate() {
        if (++budgetUpdateCounter >= BUDGET_UPDATE_INTERVAL) {
            updateBudget(1);
            budgetUpdateCounter = 0;
        }
    }

    public boolean updateBudget(int offset) {
        int budget = this.entityData.get(BUDGET) + offset;
        if (budget < 0) return false;
        this.entityData.set(BUDGET, budget);
        return true;
    }

    @Nullable
    public Animal getNearestAnimal() {
        var range = this.entityData.get(RADIUS);
        var box = this.getBoundingBox().inflate(range);
        var entities = this.level().getEntitiesOfClass(Animal.class, box);

        var self = this.getUUID();
        return entities.stream()
                .filter(e -> !e.getUUID().equals(self)) // Exclude self
                .filter(e -> e.getHealth() > 1.0)
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElse(null);
    }

//    public boolean updateBudget(int offset) {
//        int budget = this.entityData.get(BUDGET) + offset;
//        if (budget < 0) {
//            return false;
//        }
//
//        this.entityData.set(BUDGET, budget);
//        return true;
//    }

    public boolean hasBudget() {
        return this.entityData.get(BUDGET) != 0;
    }

    public void tryToAcquireTarget() {
        var acquireRange = 1000.0;
        if (this.target != null && this.target.distanceToSqr(this) < acquireRange) {
            return;
        }

        var cond = TargetingConditions.forNonCombat().range(acquireRange);
        this.target = level().getNearestPlayer(cond, this);
    }

    @Nullable
    public Player getTarget() {
        return this.target;
    }

    @Nullable
    public boolean hasTarget() {
//        System.out.println("target? " + (target != null));
        return this.target != null;
    }

    public static boolean canSpawn(EntityType<Ethereal> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
        return false;
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
                level.getChunkAt(block.west(16))
        );
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
            this.setInvisible(true);
            possessedEntity = target;
            hasPossessedEntity = true;
            updatedPossessedEntityGoals = false;
            this.queueGoalsToUpdate();
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

//    private final Set<Goal> goalsToAddQueue = new HashSet<>();
//    private final Set<Goal> goalsToRemoveQueue = new HashSet<>();


    private boolean shouldAddPossessedGoal() {
        // Add PossessedGoal if the entity was newly possessed and goals have not been updated
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
                List<Goal> currentGoals = new ArrayList<>(this.possessedEntity.goalSelector.getAvailableGoals().stream().map(WrappedGoal::getGoal).collect(Collectors.toList()));

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
        return nearestAnimal != null && this.distanceToSqr(nearestAnimal) < this.distanceToSqr(possessedEntity);
    }

    public Entity getPossessedEntity() {
        return this.possessedEntity;
    }
}