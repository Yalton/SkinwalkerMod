package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ethereal.Ethereal;
import com.yalt.skinwalker.events.DelayedTaskScheduler;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class InvokeGoal extends EtherealGoal {
    private static final int COOLDOWN_DURATION = 3 * 60 * 1000; // 5 minutes in milliseconds
    private long lastUsageTime = 0;
    private static final int COST_TO_INVOKE = 3;
    private static final int RANDOM_RANGE = 10;
    private static final int RANDOM_OFFSET_Y = 3;
    private static final int RANDOM_OFFSET = 5;
    private static final int GLOWING_EFFECT_DURATION = 20 * 10; // 10 seconds in ticks
    private static final int DESPAWN_DELAY = 20 * 10; // 10 seconds in ticks

    private static final List<EntityType<?>> HOSTILE_ENTITIES = Arrays.asList(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.CAVE_SPIDER,
            EntityType.SPIDER,
            EntityType.WITCH,
            EntityType.PIGLIN,
            EntityType.CREEPER,
            EntityType.ENDERMAN,
            EntityType.SLIME,
            EntityType.MAGMA_CUBE,
            EntityType.BLAZE,
            EntityType.WITHER_SKELETON,
            EntityType.STRAY,
            EntityType.HUSK,
            EntityType.DROWNED,
            EntityType.PHANTOM,
            EntityType.VEX);

    public InvokeGoal(Ethereal ethereal) {
        super(ethereal);
    }

    @Override
    public boolean canUse() {
        // Check if enough time has passed since the last usage
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUsageTime < COOLDOWN_DURATION) {
            return false;
        }

        return super.canUse() && ethereal.hasPossessedEntity && ethereal.hasTarget();
    }

    @Override
    public void start() {
        super.start();
        lastUsageTime = System.currentTimeMillis();


        // Get the target player's position
        Player targetPlayer = ethereal.getTarget();
        Vec3 playerPosition = targetPlayer.position();

        // Generate a random position near the player to spawn the hostile entity
        Random rand = new Random();
        double offsetX = rand.nextInt(RANDOM_RANGE) - RANDOM_OFFSET;
        double offsetY = rand.nextInt(RANDOM_OFFSET_Y);
        double offsetZ = rand.nextInt(RANDOM_RANGE) - RANDOM_OFFSET;

        Vec3 spawnPosition = new Vec3(playerPosition.x + offsetX, playerPosition.y + offsetY,
                playerPosition.z + offsetZ);

        EntityType<?> entityTypeToSpawn = HOSTILE_ENTITIES.get(rand.nextInt(HOSTILE_ENTITIES.size()));

        // Spawn the hostile entity
        spawnHostileEntity(entityTypeToSpawn, spawnPosition);

        // Update the budget
        ethereal.updateBudget(-COST_TO_INVOKE);
    }

    private void spawnHostileEntity(EntityType<?> entityType, Vec3 position) {
        this.playNoise();
        System.out.println("Spawning Hostile Entity");
        // Create the entity instance
        Entity entity = entityType.create(ethereal.level());

        if (entity == null) {
            return;
        }

        // Set the entity's position
        entity.setPos(position.x, position.y, position.z);

        // Apply the Glowing effect to the entity
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOWING_EFFECT_DURATION));
        }

        // Spawn the entity into the world
        ethereal.level().addFreshEntity(entity);

        // Schedule the entity to be removed after a certain duration
        DelayedTaskScheduler.scheduleTask(() -> entity.remove(Entity.RemovalReason.DISCARDED), DESPAWN_DELAY);
    }
}
