package com.yalt.skinwalker.entity.goal;

import com.yalt.skinwalker.entity.walker.SkinWalkerEntity;
import com.yalt.skinwalker.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Random;

public class ChaseGoal extends Goal {
    private final SkinWalkerEntity mob;
    private long lastSoundTime = 0;
    private final double speed;
    private LivingEntity target;

    public ChaseGoal(SkinWalkerEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        if (mob.isAggro()) {
            return true;
        }
        List<Player> players = mob.level.getEntitiesOfClass(Player.class, mob.getBoundingBox().inflate(1000.0f)); // Any distance
        if (players.isEmpty()) {
            return false;
        }

        Player closestPlayer = players.get(0);

        if (closestPlayer != null) {
            double distance = closestPlayer.distanceTo(mob);
            if (distance < 8.0D) {
                return true;
            }
        }
        target = players.get(0);
        return false;
    }

    @Override
    public void start() {
        mob.setBaiting(false);
        mob.setStalking(false);
        mob.setAggro(true);
        mob.setFourLegged(true);
        mob.setSprinting(true);
        System.out.println("Using Chasing Goal");
        chasePlayer();
    }

    @Override
    public void stop() {
        mob.setSprinting(false);
        mob.setFourLegged(false);
        target = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (target == null) {
            return;
        }
        if (mob.distanceToSqr(target) <= 1.0) {
            attackPlayer();
        } else {
            chasePlayer();
        }
    }

    private void chasePlayer() {
        if (target != null) {
            playNoise();
            breakBlocksInPath();
            mob.getNavigation().moveTo(target, speed);
        } else {
            return; // No target
        }
    }

    private void attackPlayer() {
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

        // Move one block up from the current position
        BlockPos blockAbove = blockInPath.above(); // Get the block above
        BlockState blockAboveState = level.getBlockState(blockAbove);

        if (!blockAboveState.isAir() && blockAboveState.getDestroySpeed(level, blockAbove) >= 0) {
            level.destroyBlock(blockAbove, false);
        }
    }


    private void playNoise() {
        System.out.println("Playing Noise");
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSoundTime < 3000 && lastSoundTime != 0) {
            System.out.println("Used sound to recently currentTime - lastSoundTime: " + (currentTime - lastSoundTime));
            return;
        }

        Random random = new Random();
        int randomNumber = random.nextInt(3) + 1;

        switch (randomNumber) {
            case 1:
                mob.playEntitySound((SoundEvent) ModSounds.SKINWALKER_SOUND1.get(), 0.5F, 1.0F);
                return;
            case 2:
                mob.playEntitySound((SoundEvent) com.yalt.skinwalker.sound.ModSounds.SKINWALKER_SOUND2.get(), 0.5F, 1.0F);
                // Perform action for case 2
                return;
            case 3:
                mob.playEntitySound((SoundEvent) com.yalt.skinwalker.sound.ModSounds.SKINWALKER_SOUND3.get(), 0.5F, 1.0F);
                // Perform action for case 3
            default:
                System.out.println("Unexpected number generated.");
                return;
        }
    }
}
