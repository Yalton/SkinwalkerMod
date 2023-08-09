package com.yalt.skinwalker.entity.custom;

import com.yalt.skinwalker.sound.SkinWalkerSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SkinWalkerChaseGoal extends Goal {
    private final SkinWalkerEntity mob;
    private final double speed;
    private LivingEntity target;
    private int currentChaseSoundIndex = 0;


    public SkinWalkerChaseGoal(SkinWalkerEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        List<Player> players = mob.level.getEntitiesOfClass(Player.class, mob.getBoundingBox().inflate(1000.0f)); // Any distance
        if (players.isEmpty()) {
            return false;
        }

        target = players.get(0);
        return true;
    }

    @Override
    public void start() {
        chasePlayer();
    }

    @Override
    public void stop() {
        target = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (mob.distanceToSqr(target) <= 2.0) { // Attack range
            attackPlayer();
        } else {
            chasePlayer();
            breakBlocksInPath();
        }
    }

    private void chasePlayer() {
        mob.getNavigation().moveTo(target, speed);
    }

    private void attackPlayer() {
        // Logic to attack the player
        mob.doHurtTarget(target);
    }

    private void breakBlocksInPath() {
        Level level = (Level) mob.level; // Cast to Level
        BlockPos mobPos = mob.blockPosition();
        BlockPos targetPos = target.blockPosition();
        double deltaX = targetPos.getX() - mobPos.getX();
        double deltaY = targetPos.getY() - mobPos.getY();
        double deltaZ = targetPos.getZ() - mobPos.getZ();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        // Normalize the direction
        deltaX /= distance;
        deltaY /= distance;
        deltaZ /= distance;

        // Move one block in the direction
        BlockPos blockInPath = new BlockPos((int) (mobPos.getX() + deltaX), (int) (mobPos.getY() + deltaY), (int) (mobPos.getZ() + deltaZ));
        BlockState blockState = level.getBlockState(blockInPath);

        if (!blockState.isAir() && blockState.getDestroySpeed(level, blockInPath) >= 0) {
            level.destroyBlock(blockInPath, false);
        }
    }

    private void playNoise() {
        if (currentChaseSoundIndex < SkinWalkerSounds.SKINWALKER_SOUNDS.length) {
            int randomIndex = mob.getRandom().nextInt(SkinWalkerSounds.BAIT_SOUNDS.length); // Choose a random index
            Level level = (Level) mob.level; // Make sure to get the level correctly
            level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SkinWalkerSounds.SKINWALKER_SOUNDS[randomIndex].get(), SoundSource.PLAYERS, 2.0F, 1.0F);
            currentChaseSoundIndex++;
        }
    }
}
