package com.yalt.skinwalker.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation.LoopType;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
public class SkinWalkerEntity extends Monster implements GeoEntity {

    public Level level;
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private boolean isFourLegged;
    public long baitingTime;
    private int timer;
    private EntityType<?> currentTransformation;
    public static List<EntityType<?>> TRANSFORMABLE_MOBS = null;
    public static EntityDataAccessor<Boolean> FOURLEGGED_ACCESSOR = SynchedEntityData.defineId(SkinWalkerEntity.class, EntityDataSerializers.BOOLEAN);
    public static EntityDataAccessor<Boolean> STALKING_ACCESSOR = SynchedEntityData.defineId(SkinWalkerEntity.class, EntityDataSerializers.BOOLEAN);
    public static EntityDataAccessor<Boolean> AGGRO_ACCESSOR = SynchedEntityData.defineId(SkinWalkerEntity.class, EntityDataSerializers.BOOLEAN);
    public static EntityDataAccessor<Boolean> TRANSFORMED_ACCESSOR = SynchedEntityData.defineId(SkinWalkerEntity.class, EntityDataSerializers.BOOLEAN);
    public static EntityDataAccessor<Boolean> BAITING_ACCESSOR = SynchedEntityData.defineId(SkinWalkerEntity.class, EntityDataSerializers.BOOLEAN);


    private RawAnimation IDLE;
    private RawAnimation WALK;
    private RawAnimation SPRINT;
    private RawAnimation FOUR_LEGGED_TRANSITION_TO;
    private RawAnimation FOUR_LEGGED_IDLE;
    private RawAnimation FOUR_LEGGED_WALK;
    private RawAnimation FOUR_LEGGED_SPRINT;
    private RawAnimation FOUR_LEGGED_TRANSITION_BACK;

    public SkinWalkerEntity(EntityType<? extends SkinWalkerEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.level = pLevel; // Explicitly set the level field

//        this.TRANSFORMABLE_MOBS = Arrays.asList(EntityType.COW, EntityType.SHEEP, EntityType.PIG);
        this.currentTransformation = null;
        this.FOUR_LEGGED_TRANSITION_TO = RawAnimation.begin().then("animation.Skinwalker.fourLeggedTransition", LoopType.HOLD_ON_LAST_FRAME);
        this.isFourLegged = false;
        this.baitingTime = 0;
        this.timer = 0;
        // this.defineSynchedData();
        // this.registerGoals();
    }
    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100D)
                .add(Attributes.ATTACK_DAMAGE, 4.0f)
                .add(Attributes.ATTACK_SPEED, 4.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.4f).build();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FOURLEGGED_ACCESSOR, false);
        this.entityData.define(STALKING_ACCESSOR, false);
        this.entityData.define(AGGRO_ACCESSOR, false);
        this.entityData.define(TRANSFORMED_ACCESSOR, false);
        this.entityData.define(BAITING_ACCESSOR, false);
    }



    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SkinWalkerBaitingGoal(this, 1.0D, 30.0F, 10.0F));
        this.goalSelector.addGoal(2, new SkinWalkerStalkGoal(this, 1.0D, 30.0F, 10.0F));
        this.goalSelector.addGoal(3, new SkinWalkerChaseGoal(this, 1.0D));

        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
//        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Pig.class, true));
//        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Cow.class, true));
//        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Sheep.class, true));
//        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Chicken.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        this.timer++;
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return new EntityDimensions(2F, 3.0F, true);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

//    protected void playStepSound(BlockPos pos, BlockState blockIn) {
//        this.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 0.15F, 1.0F);
//    }

//    protected SoundEvent getAmbientSound() {
//        return (SoundEvent) ModSounds.SKINWALKER_TALKING1.get();
//    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.DOLPHIN_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.DOLPHIN_DEATH;
    }

    protected float getSoundVolume() {
        return 0.2F;
    }

    public void playEntitySound(SoundEvent soundEvent, float volume, float pitch) {
        System.out.println("Playing sound: " + soundEvent);
        this.playSound(soundEvent, volume, pitch);
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        // Check every 30 seconds (600 ticks = 30 seconds)
//        if (this.timer >= 600) {
//            // Reset timer
//            timer = 0;
//
//            // 50% chance to switch to four-legged state
//            this.isFourLegged = this.random.nextBoolean();
//            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.Skinwalker.fourLeggedTransition", LoopType.HOLD_ON_LAST_FRAME));
//        }
        if (isFourLegged) {
            if (tAnimationState.isMoving()) {
                tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.Skinwalker.fourLeggedWalk", LoopType.LOOP));
            } else {
                tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.Skinwalker.fourLeggedidle", LoopType.LOOP));
            }
        } else {
            if(tAnimationState.isMoving()) {
                tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.Skinwalker.walk", LoopType.LOOP));
                return PlayState.CONTINUE;
            }

            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.Skinwalker.idle", LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static boolean canSpawn(EntityType<SkinWalkerEntity> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
        return false;
    }

    public EntityType getCurrentTransformation()
    {
        return this.currentTransformation;
    }
    public void transformInto(EntityType<?> entityType) {
        this.currentTransformation = entityType;
    }

//    public float distanceToSqr(ServerPlayer closestPlayer) {
//        if (closestPlayer == null) return Float.MAX_VALUE;
//
//        double dx = this.getX() - closestPlayer.getX();
//        double dy = this.getY() - closestPlayer.getY();
//        double dz = this.getZ() - closestPlayer.getZ();
//
//        return (float) (dx * dx + dy * dy + dz * dz);
//    }

    public boolean isBaiting() {
        return this.entityData.get(BAITING_ACCESSOR);
    }
    public void setBaiting(boolean value) {
        this.entityData.set(BAITING_ACCESSOR, value);
    }
    public boolean isFourLegged() {
        return this.entityData.get(FOURLEGGED_ACCESSOR);
    }
    public void setFourLegged(boolean value) {
        this.entityData.set(FOURLEGGED_ACCESSOR, value);
    }
    public boolean isStalking() {
        return this.entityData.get(STALKING_ACCESSOR);
    }
    public void setStalking(boolean value) {
        this.entityData.set(STALKING_ACCESSOR, value);
    }
    public boolean isAggro() {
        return this.entityData.get(BAITING_ACCESSOR);
    }
    public void setAggro(boolean value) {
        this.entityData.set(AGGRO_ACCESSOR, value);
    }
    public boolean isTransformed() {
        return this.entityData.get(TRANSFORMED_ACCESSOR);
    }
    public void setTransformed(boolean value) {
        this.entityData.set(TRANSFORMED_ACCESSOR, value);
    }
}