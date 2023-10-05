package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ethereal.Ethereal;
import net.minecraft.world.entity.player.Player;

public class StayNearPlayerGoal extends EtherealGoal {
    private Player player;
    private final double radius = 60.0;
    private boolean withinRadius = false;

    public StayNearPlayerGoal(Ethereal ethereal) {
        super(ethereal);
    }

    @Override
    public boolean canUse() {
        this.player = ethereal.getTarget();
        if (player != null) {
            return !withinRadius;
        }
        System.out.println("Cannot navigate to player player is null");
        return false;
    }

    @Override
    public void start() {
        System.out.println("Ethereal Navigating to Player");
        moveToPlayer();
    }

    @Override
    public void tick() {
        if (!withinRadius) {
            moveToPlayer();
        }
    }

    private void moveToPlayer() {
        ethereal.getNavigation().moveTo(player.getX(), player.getY(), player.getZ(), 1.0);
        double distance = ethereal.distanceToSqr(player);
        if (distance <= radius * radius) {
            withinRadius = true;
        } else {
            withinRadius = false;
        }
    }
}