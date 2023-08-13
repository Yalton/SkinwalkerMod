package com.yalt.skinwalker.entity.goal;

import com.yalt.skinwalker.entity.walker.SkinWalkerEntity;
import com.yalt.skinwalker.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Random;

public class BaitingGoal extends Goal {
    private final SkinWalkerEntity mob;
    private long lastSoundTime = 0;
    private static final double TARGET_THRESHOLD = 4.0;

    private double targetX, targetY, targetZ; // Target location for mob
    private LivingEntity targetPlayer; // Target Player to keep distance from
    private double targetSelfX, targetSelfY, targetSelfZ; // Initial coordinates of mob
    //    private double distanceToPlayer;
    private boolean atTargetLocation = false;

    private final double speed;
    private final float maxDistance;
    private final float minDistance;


    public BaitingGoal(SkinWalkerEntity mob, double speed, float maxDistance, float minDistance) {
        this.mob = mob;
        this.speed = speed;
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
    }

    @Override
    public boolean canUse() {

        if (mob.isBaiting() || mob.isAggro() || mob.isStalking()) {
            return false;
        }

        List<Player> players = mob.level.getEntitiesOfClass(Player.class, mob.getBoundingBox().inflate(maxDistance));
        if (players.isEmpty()) {
            return false;
        }

        Player closestPlayer = players.get(0);
        if (mob.distanceToSqr(closestPlayer) <= minDistance * minDistance || mob.distanceToSqr(closestPlayer) >= 40 * 40) {
            return false;
        }
        if (mob.baitingTime < 6000) {
            targetPlayer = closestPlayer;
//            distanceToPlayer = mob.distanceToSqr(closestPlayer);
            targetSelfX = mob.getX();
            targetSelfY = mob.getY();
            targetSelfZ = mob.getZ();

            return true;
        }
        return false;
    }

    @Override
    public void start() {
        //mob.transform();
        mob.setBaiting(true);
        if (!atTargetLocation) {
            int minDistanceFromPlayer = 40;
            int maxDistanceFromPlayer = 60;
            double distance = minDistanceFromPlayer + new Random().nextFloat() * (maxDistanceFromPlayer - minDistanceFromPlayer);
            double angle = new Random().nextFloat() * 2 * Math.PI;
            targetX = targetSelfX + distance * Math.cos(angle);
            targetY = targetSelfY;
            targetZ = targetSelfZ + distance * Math.sin(angle);
            System.out.println("Using Baiting Goal navigating to " + targetX + ", " + targetY + ", " + targetZ);

            mob.getNavigation().moveTo(targetX, targetY, targetZ, speed);
        }
    }


    @Override
    public void stop() {
        mob.setBaiting(false);
        targetPlayer = null;
        atTargetLocation = false;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        double distanceToTarget = mob.distanceToSqr(targetX, targetY, targetZ);

        if (!atTargetLocation && distanceToTarget <= TARGET_THRESHOLD * TARGET_THRESHOLD) {
            System.out.println("Reached target location");
            atTargetLocation = true;
        }

        if (atTargetLocation) {
            if (mob.distanceToSqr(targetPlayer) >= 100 * 100) {
                atTargetLocation = false;
                start();
            } else {
                mob.baitingTime++;
                playNoise();
                watchPlayer();
            }
        } else {
            mob.getNavigation().moveTo(targetX, targetY, targetZ, speed);
            System.out.println("Not at location yet Distance to target" + Math.sqrt(distanceToTarget));
            System.out.println("Target coordinates: " + targetX + ", " + targetY + ", " + targetZ);
            System.out.println("Mob coordinates: " + mob.getX() + ", " + mob.getY() + ", " + mob.getZ());
        }

    }

    private void watchPlayer() {
        if (targetPlayer != null) {
//            System.out.println("Watching Player");
            mob.getLookControl().setLookAt(targetPlayer, mob.getMaxHeadYRot(), mob.getMaxHeadXRot());
        }
    }

    private void playNoise() {
        System.out.println("Playing Noise");
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSoundTime < 30000 && lastSoundTime != 0) {
            System.out.println("Used sound to recently currentTime - lastSoundTime: " + (currentTime - lastSoundTime));
            return;
        }

        Random random = new Random();
        int randomNumber = random.nextInt(2) + 1;
        switch (randomNumber) {
            case 1 -> {
                mob.playEntitySound((SoundEvent) com.yalt.skinwalker.sound.ModSounds.SKINWALKER_BAIT1.get(), 4.0F, 1.0F);
                lastSoundTime = currentTime; // Update the timestamp
            }
            case 2 -> {
                mob.playEntitySound((SoundEvent) ModSounds.SKINWALKER_BAIT2.get(), 4.0F, 1.0F);
                lastSoundTime = currentTime; // Update the timestamp
            }
            default -> {
                System.out.println("Unexpected number generated.");
            }
        }
    }
}