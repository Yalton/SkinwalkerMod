package com.yalt.skinwalker.entity.ethereal.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;

public class PossessedGoal extends Goal {
    private static final float MAX_LOOK_ANGLE = 10.0F;
    private static final double MIN_DISTANCE = 12.0D;
    
    private final Mob mob;
    private Player nearestPlayer;
    private final double followSpeed;
    private final float maxDist;

    public PossessedGoal(Mob mob, double followSpeed, float maxDist) {
        this.mob = mob;
        this.followSpeed = followSpeed;
        this.maxDist = maxDist;
    }

    @Override
    public boolean canUse() {
        this.nearestPlayer = this.mob.level().getNearestPlayer(mob, maxDist);
        return this.nearestPlayer != null;
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(this.nearestPlayer, MAX_LOOK_ANGLE, this.mob.getMaxHeadXRot());

        double distanceToPlayer = this.mob.distanceTo(this.nearestPlayer);

        if (distanceToPlayer > MIN_DISTANCE) {
            // Move closer to maintain a minimum distance of 12 blocks
            this.mob.getNavigation().moveTo(this.nearestPlayer, this.followSpeed);
        } else if (distanceToPlayer < MIN_DISTANCE) {
            // Move away to maintain a minimum distance of 12 blocks
            double dx = this.mob.getX() - this.nearestPlayer.getX();
            double dz = this.mob.getZ() - this.nearestPlayer.getZ();
            this.mob.getNavigation().moveTo(this.mob.getX() + dx, this.mob.getY(), this.mob.getZ() + dz, this.followSpeed);
        } else {
            // Stop moving if the distance is exactly 12 blocks
            this.mob.getNavigation().stop();
        }
    }

}
