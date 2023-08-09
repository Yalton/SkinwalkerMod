package com.yalt.skinwalker.entity.custom;

import com.yalt.skinwalker.sound.SkinWalkerSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
public class SkinWalkerBaitingGoal extends Goal {
    private final SkinWalkerEntity mob;
    private final double speed;
    private final float maxDistance;
    private final float minDistance;
    private LivingEntity target;
    private int currentBaitSoundIndex = 0;



    public SkinWalkerBaitingGoal(SkinWalkerEntity mob, double speed, float maxDistance, float minDistance) {
        this.mob = mob;
        this.speed = speed;
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
    }

    @Override
    public boolean canUse() {
        Player closestPlayer = null;
        if (mob.baitingTime > 0) {
            List<Player> players = mob.level.getEntitiesOfClass(Player.class, mob.getBoundingBox().inflate(maxDistance));
            if (players.isEmpty()) {
                return false;
            }

            closestPlayer = players.get(0);
            if (mob.distanceToSqr(closestPlayer) <= minDistance * minDistance) {
                return false;
            }

        }

        this.target = closestPlayer;
        return true;
    }

    public boolean isActive() {
        return canUse();
    }


    @Override
    public void start() {
        mob.getNavigation().moveTo(target, speed);
        playNoise(); // Play noise
    }

    @Override
    public void stop() {
        target = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (mob.distanceToSqr(target) <= minDistance * minDistance) {
            stopNoise(); // Stop playing noise
        } else {
            playNoise(); // Play noise
        }

        mob.getNavigation().moveTo(target, speed);
    }

    private void playNoise() {
        if (SkinWalkerSounds.BAIT_SOUNDS.length > 0) {
            int randomIndex = mob.getRandom().nextInt(SkinWalkerSounds.BAIT_SOUNDS.length); // Choose a random index
            Level level = (Level) mob.level; // Make sure to get the level correctly
            level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SkinWalkerSounds.BAIT_SOUNDS[randomIndex].get(), SoundSource.PLAYERS, 2.0F, 1.0F);
        }
    }

    private void stopNoise() {
        // Logic to stop playing noise
    }
}