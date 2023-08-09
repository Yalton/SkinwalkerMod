package com.yalt.skinwalker.entity.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class SkinWalkerStalkGoal extends Goal {
    private final SkinWalkerEntity mob;
    private final double speed;
    private final float maxDistance;
    private final float minDistance;
    private LivingEntity target;

    public SkinWalkerStalkGoal(SkinWalkerEntity mob, double speed, float maxDistance, float minDistance) {
        this.mob = mob;
        this.speed = speed;
        this.maxDistance = maxDistance; // 30 blocks
        this.minDistance = minDistance; // 10 blocks
    }

    @Override
    public boolean canUse() {
        List<Player> players = mob.level.getEntitiesOfClass(Player.class, mob.getBoundingBox().inflate(maxDistance));
        if (players.isEmpty()) {
            return false;
        }

        Player closestPlayer = players.get(0);
        if (mob.distanceToSqr(closestPlayer) <= minDistance * minDistance) {
            target = closestPlayer;
            return true;
        }

        target = null;
        return false;
    }

    @Override
    public void start() {
        watchPlayer(); // Watch the player
    }

    @Override
    public void stop() {
        target = null;
    }

    @Override
    public void tick() {
        if (mob.distanceToSqr(target) < (minDistance * minDistance)) {
            retreat(); // Retreat back to at least 30 blocks away
        } else {
            watchPlayer(); // Watch the player
        }
    }

    private void watchPlayer() {
        mob.getLookControl().setLookAt(target, mob.getMaxHeadYRot(), mob.getMaxHeadXRot());
    }

    private void retreat() {
        // Logic to move the mob away from the player to a distance of at least 30 blocks
        double retreatX = mob.getX() + (mob.getX() - target.getX());
        double retreatY = mob.getY() + (mob.getY() - target.getY());
        double retreatZ = mob.getZ() + (mob.getZ() - target.getZ());
        mob.getNavigation().moveTo(retreatX, retreatY, retreatZ, speed);
    }
}
