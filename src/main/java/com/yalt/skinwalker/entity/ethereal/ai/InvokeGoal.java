package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ethereal.Ethereal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class InvokeGoal extends EtherealGoal {
    public InvokeGoal(Ethereal ethereal) {
        super(ethereal);
    }

    private static final int COST_TO_INVOKE = 1;

    @Override
    public boolean canUse() {
        return super.canUse() && ethereal.hasPossessedEntity && ethereal.hasTarget();
    }

    private static final List<EntityType<?>> HOSTILE_ENTITIES = Arrays.asList(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.CAVE_SPIDER,
            EntityType.SPIDER,
            EntityType.WITCH,
            EntityType.PIGLIN

    );


    @Override
    public void start() {
        super.start();

        // Get the target player's position
        Player targetPlayer = ethereal.getTarget();
        Vec3 playerPosition = targetPlayer.position();

        // Generate a random position near the player to spawn the hostile entity
        Random rand = new Random();
        double offsetX = rand.nextInt(10) - 5;
        double offsetY = rand.nextInt(3);
        double offsetZ = rand.nextInt(10) - 5;

        Vec3 spawnPosition = new Vec3(playerPosition.x + offsetX, playerPosition.y + offsetY, playerPosition.z + offsetZ);

        EntityType<?> entityTypeToSpawn = HOSTILE_ENTITIES.get(rand.nextInt(HOSTILE_ENTITIES.size()));

        System.out.println("Spawning Enemy");

        // Spawn the hostile entity
        spawnHostileEntity(entityTypeToSpawn, spawnPosition);

        // Update the budget
        ethereal.updateBudget(-COST_TO_INVOKE);
    }

    private void spawnHostileEntity(EntityType<?> entityType, Vec3 position) {
        // Create the entity instance
        Entity entity = entityType.create(ethereal.level());

        if (entity == null) {
            return;
        }

        // Set the entity's position
        entity.setPos(position.x, position.y, position.z);

        // Spawn the entity into the world
        ethereal.level().addFreshEntity(entity);
    }
}
