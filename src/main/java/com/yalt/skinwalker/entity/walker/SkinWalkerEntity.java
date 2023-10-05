package com.yalt.skinwalker.entity.walker;

import com.yalt.skinwalker.entity.goal.ChaseGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class SkinWalkerEntity extends Monster implements GeoEntity {

    public final Level level;
    private final AnimatableInstanceCache cache;
    public long baitingTime;
    private int timer;
    private Entity currentTransformation;
    public static final List<EntityType<?>> TRANSFORMABLE_MOBS = Arrays.asList(EntityType.COW, EntityType.SHEEP, EntityType.PIG, EntityType.CHICKEN);
    public static final EntityDataAccessor<Boolean> FOURLEGGED_ACCESSOR = SynchedEntityData.defineId(SkinWalkerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> STALKING_ACCESSOR = SynchedEntityData.defineId(SkinWalkerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> AGGRO_ACCESSOR = SynchedEntityData.defineId(SkinWalkerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SPRINTING_ACCESSOR = SynchedEntityData.defineId(SkinWalkerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> TRANSFORMED_ACCESSOR = SynchedEntityData.defineId(SkinWalkerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> BAITING_ACCESSOR = SynchedEntityData.defineId(SkinWalkerEntity.class, EntityDataSerializers.BOOLEAN);
    private final RawAnimation FOUR_LEGGED_TRANSITION_TO;
    // private final RawAnimation IDLE;
    // private final RawAnimation WALK;
    // private final RawAnimation SPRINT;
    // private final RawAnimation FOUR_LEGGED_IDLE;
    // private final RawAnimation FOUR_LEGGED_WALK;
    // private final RawAnimation FOUR_LEGGED_SPRINT;
    // private final RawAnimation FOUR_LEGGED_TRANSITION_BACK;
    private final Map<String, AnimationController<SkinWalkerEntity>> animationControllers;

    public SkinWalkerEntity(EntityType<? extends SkinWalkerEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.level = pLevel;
        this.cache = new SingletonAnimatableInstanceCache(this);
        this.baitingTime = 0;
        this.timer = 0;
        this.animationControllers = new HashMap<>();
        this.FOUR_LEGGED_TRANSITION_TO = RawAnimation.begin().then("animation.Skinwalker.fourLeggedTransition", LoopType.HOLD_ON_LAST_FRAME);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100D)
                .add(Attributes.ATTACK_DAMAGE, 8.0f)
                .add(Attributes.ATTACK_SPEED, 4.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.MOVEMENT_SPEED, 0.2f).build();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FOURLEGGED_ACCESSOR, false);
        this.entityData.define(STALKING_ACCESSOR, false);
        this.entityData.define(AGGRO_ACCESSOR, false);
        this.entityData.define(TRANSFORMED_ACCESSOR, false);
        this.entityData.define(BAITING_ACCESSOR, false);
        this.entityData.define(SPRINTING_ACCESSOR, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ChaseGoal(this, 1.0D));
    }

    @Override
    public void tick() {
        super.tick();
        this.timer++;
        if (this.timer >= 600) {
            this.timer = 0;
            this.setFourLegged(this.random.nextBoolean());
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return new EntityDimensions(2F, 3.0F, true);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    public Map<String, AnimationController<SkinWalkerEntity>> getAnimationControllers() {
        return this.animationControllers;
    }

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
        this.playSound(soundEvent, volume, pitch);
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (this.isFourLegged()) {
            if (tAnimationState.isMoving()) {
                if (this.isSprinting()) {
                    tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.Skinwalker.fourLeggedSprint", LoopType.LOOP));
                } else {
                    tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.Skinwalker.fourLeggedWalk", LoopType.LOOP));
                }
            } else {
                tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.Skinwalker.fourLeggedidle", LoopType.LOOP));
            }
        } else {
            if (tAnimationState.isMoving()) {
                if (this.isSprinting()) {
                    tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.Skinwalker.sprint", LoopType.LOOP));
                } else {
                    tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.Skinwalker.walk", LoopType.LOOP));
                }
            } else {
                tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.Skinwalker.idle", LoopType.LOOP));
            }
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

    public Entity getCurrentTransformation() {
        return this.currentTransformation;
    }

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
        boolean wasFourLegged = this.isFourLegged();
        this.entityData.set(FOURLEGGED_ACCESSOR, value);

        if (!wasFourLegged && value) {
            AnimationController<SkinWalkerEntity> controller = this.getAnimationControllers().get("controller");
            if (controller != null) {
                controller.setAnimation(FOUR_LEGGED_TRANSITION_TO);
            }
        }
    }

    // public boolean isStalking() {
    //     return this.entityData.get(STALKING_ACCESSOR);
    // }

    // public void setStalking(boolean value) {
    //     this.entityData.set(STALKING_ACCESSOR, value);
    // }

    // // public boolean isAggro() {
    // //     return this.entityData.get(AGGRO_ACCESSOR);
    // // }

    // // public void setAggro(boolean value) {
    // //     this.entityData.set(AGGRO_ACCESSOR, value);
    // // }

    // public boolean isTransformed() {
    //     return this.entityData.get(TRANSFORMED_ACCESSOR);
    // }

    // public void setTransformed(boolean value) {
    //     this.entityData.set(TRANSFORMED_ACCESSOR, value);
    // }

    // public boolean isSprinting() {
    //     return this.entityData.get(SPRINTING_ACCESSOR);
    // }

    // public void setSprinting(boolean value) {
    //     this.entityData.set(SPRINTING_ACCESSOR, value);
    //     this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(value ? 0.6f : 0.4f);
    // }
}