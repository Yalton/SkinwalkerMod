package com.yalt.skinwalker.entity.goal;

import com.yalt.skinwalker.entity.walker.SkinWalkerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class StalkGoal extends Goal {
    private final SkinWalkerEntity mob;
    //private final Mob mob;
    private final double speed;
    private final float maxDistance;
    private final float minDistance;
    private LivingEntity target;
    private int currentStalkSoundIndex = 0;


    public StalkGoal(SkinWalkerEntity mob, double speed, float maxDistance, float minDistance) {
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

        if (mob.baitingTime > 6000) {
            Player closestPlayer = players.get(0);
            if (mob.distanceToSqr(closestPlayer) <= minDistance * minDistance) {
                target = closestPlayer;
                return true;
            }
        }
        target = null;
        return false;
    }

    @Override
    public void start() {
        mob.setStalking(true);

        System.out.println("Using Stalking Goal");
        watchPlayer(); // Watch the player
    }

    @Override
    public void stop() {
        mob.setStalking(false);
        target = null;
    }

    @Override
    public void tick() {
        if (target == null) {
            return;
        }
        if (mob.distanceToSqr(target) < (minDistance * minDistance)) {
            retreat(); // Retreat back to at least 30 blocks away
        } else {
            watchPlayer(); // Watch the player
        }
    }

    private void watchPlayer() {
        if (target == null) {
            return;
        }
        mob.getLookControl().setLookAt(target, mob.getMaxHeadYRot(), mob.getMaxHeadXRot());
    }

    private void retreat() {
        // Logic to move the mob away from the player to a distance of at least 30 blocks
        double retreatX = mob.getX() + (mob.getX() - target.getX());
        double retreatY = mob.getY() + (mob.getY() - target.getY());
        double retreatZ = mob.getZ() + (mob.getZ() - target.getZ());
        mob.getNavigation().moveTo(retreatX, retreatY, retreatZ, speed);
    }

    private void playNoise() {
//        if (currentStalkSoundIndex < SkinWalkerSounds.TALKING_SOUNDS.length) {
//            int randomIndex = mob.getRandom().nextInt(SkinWalkerSounds.TALKING_SOUNDS.length); // Choose a random index
//            Level level = (Level) mob.level; // Make sure to get the level correctly
//            level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SkinWalkerSounds.TALKING_SOUNDS[randomIndex].get(), SoundSource.PLAYERS, 2.0F, 1.0F);
//            currentStalkSoundIndex++;
//        }
    }
}
