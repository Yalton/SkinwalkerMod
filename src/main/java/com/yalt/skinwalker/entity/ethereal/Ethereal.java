package com.yalt.skinwalker.entity.ethereal;

import com.yalt.skinwalker.entity.ethereal.ai.AggroGoal;
import com.yalt.skinwalker.entity.ethereal.ai.PossessGoal;
import com.yalt.skinwalker.entity.ethereal.ai.SabotageGoal;
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
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

public class Ethereal extends Cow {
    private Player target = null;
    private Mob actor = null;
    private HashMap<UUID, Integer> attitude;

    private static final EntityDataAccessor<Integer> BUDGET = SynchedEntityData.defineId(Ethereal.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> REGEN = SynchedEntityData.defineId(Ethereal.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RADIUS = SynchedEntityData.defineId(Ethereal.class, EntityDataSerializers.INT);

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
    public void tick() {
        super.tick();
//        tryToAcquireTarget();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BUDGET, 10);
        this.entityData.define(REGEN, 1);
        this.entityData.define(RADIUS, 25);
    }

    @Override
    protected void registerGoals() {
//        super.registerGoals();
        // this.goalSelector.addGoal(1, new FollowMobGoal());
        // this.goalSelector.addGoal(1, new InvokeGoal(this));
        this.goalSelector.addGoal(1, new AggroGoal(this));
        this.goalSelector.addGoal(2, new PossessGoal(this));
        this.goalSelector.addGoal(3, new SabotageGoal(this));
//        this.goalSelector.addGoal(4, new CurseGoal(this));
    }

    @Nullable
    public Animal getNearestAnimal() {
        var range = this.entityData.get(RADIUS);
        var box = this.getBoundingBox().inflate(range);
        var entities = this.level().getEntitiesOfClass(Animal.class, box);

        var self = this.getUUID();
        return entities.stream()
                .filter(e -> e.getUUID().equals(self))
                .filter(e -> e.getHealth() > 1.0)
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElse(null);
    }

    public boolean updateBudget(int offset) {
        int budget = this.entityData.get(BUDGET) + offset;
        if (budget < 0) {
            return false;
        }

        this.entityData.set(BUDGET, budget);
        return true;
    }

    public boolean hasBudget() {
        return this.entityData.get(BUDGET) != 0;
    }

    public void tryToAcquireTarget() {
        var acquireRange = 1000.0;
        if (target != null && target.distanceToSqr(this) < acquireRange) {
            return;
        }

        var cond = TargetingConditions.forNonCombat().range(acquireRange);
        target = level().getNearestPlayer(cond, this);
    }

    @Nullable
    public Player getTarget() {
        return target;
    }

    @Nullable
    public boolean hasTarget() {
        System.out.println("target? " + (target != null));
        return target != null;
    }

    @Nullable
    public Mob getActor() {
        return actor;
    }

    public void setActor(Mob entity) {
        this.actor = entity;
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
//    @Override
//    public boolean isPushedByFluid() {
//        return false;
//    }
//    @Override
//    public boolean isPushable() {
//        return false;
//    }


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
        // var radius = getRadius();

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

}
