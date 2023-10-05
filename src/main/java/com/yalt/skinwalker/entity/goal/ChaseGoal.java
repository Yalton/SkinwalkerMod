package com.yalt.skinwalker.entity.goal;

import com.yalt.skinwalker.entity.walker.SkinWalkerEntity;
import com.yalt.skinwalker.sound.ModSounds;
import com.yalt.skinwalker.entity.ethereal.Ethereal;
import com.yalt.skinwalker.entity.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Random;

public class ChaseGoal extends Goal {
    private final SkinWalkerEntity mob;
    private long lastSoundTime = 0;
    private final double speed;
    public Player target;
    private long chaseStartTime = 0; // New variable to keep track of chase start time
    public boolean transformIntoEthereal = false;
    public float MAX_DISTANCE = 50.0f;

    EntityType<Ethereal> EtherealEntityType = ModEntityTypes.ETHEREAL_ENTITY.get();

    public ChaseGoal(SkinWalkerEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    private Player getClosestPlayer() {
        Level level = mob.level();
        List<Player> players = level.getEntitiesOfClass(Player.class, mob.getBoundingBox().inflate(MAX_DISTANCE));
        Player closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;

        for (Player player : players) {
            double distance = mob.distanceToSqr(player);

            if (distance < closestDistance) {
                closestPlayer = player;
                closestDistance = distance;
            }
        }

        return closestPlayer;
    }

    @Override
    public boolean canUse() {
        return true; // Always able to use this goal
    }

    @Override
    public void start() {
        this.target = getClosestPlayer();
        if (this.transformIntoEthereal) {
            // Code to make the entity walk away
            Ethereal etherealEntity = new Ethereal(EtherealEntityType, mob.level());
            transform(etherealEntity);
        } else {
            // Existing code for chasing
            this.chaseStartTime = System.currentTimeMillis();
            mob.setFourLegged(false);
            mob.setSprinting(true);
            System.out.println("Using Chasing Goal");
            chasePlayer();
        }
    }

    @Override
    public void stop() {
        mob.setSprinting(false);
        mob.setFourLegged(false);
        this.target = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (target == null) {
            this.transformIntoEthereal = true;
            return;
        }

        double distanceToTarget = mob.distanceToSqr(this.target);

        if (System.currentTimeMillis() - chaseStartTime > 240000 || distanceToTarget > MAX_DISTANCE
                || this.transformIntoEthereal) {
            Ethereal etherealEntity = new Ethereal(EtherealEntityType, mob.level());
            transform(etherealEntity);
            return;
        }

        if (distanceToTarget <= 1.0) {
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
            System.out.println("Target is null");
            return; // No target
        }
    }

    private void attackPlayer() {
        mob.doHurtTarget(target);
    }

    private void breakBlocksInPath() {
        Level level = (Level) mob.level(); // Cast to Level
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

        // Create a Random object for generating the chance to break a block
        Random random = new Random();

        // Iterate over a 3 block height and 2 block width in front of the entity
        for (int y = 0; y < 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    // Calculate the block position
                    BlockPos blockInPath = new BlockPos((int) (mobPos.getX() + deltaX + x),
                            (int) (mobPos.getY() + deltaY + y),
                            (int) (mobPos.getZ() + deltaZ + z));
                    BlockState blockState = level.getBlockState(blockInPath);

                    // If the block is not air and can be destroyed, destroy it with a 20% chance
                    if (!blockState.isAir() && blockState.getDestroySpeed(level, blockInPath) >= 0
                            && random.nextFloat() < 0.05) {
                        // Destroy the block and drop the item
                        level.destroyBlock(blockInPath, true);
                    }
                }
            }
        }
    }

    private void playNoise() {
        long currentTime = System.currentTimeMillis();
        // Check if the sound was played less than 5 seconds ago
        if (currentTime - lastSoundTime < 5000 && lastSoundTime != 0) {
            return;
        }
        // Update the last sound time to the current time
        lastSoundTime = currentTime;

        Random random = new Random();
        int randomNumber = random.nextInt(3) + 1;

        switch (randomNumber) {
            case 1:
                mob.playEntitySound((SoundEvent) ModSounds.SKINWALKER_SOUND1.get(), 0.7F, 1.0F);
                return;
            case 2:
                mob.playEntitySound((SoundEvent) com.yalt.skinwalker.sound.ModSounds.SKINWALKER_SOUND2.get(), 0.7F,
                        1.0F);
                return;
            case 3:
                mob.playEntitySound((SoundEvent) com.yalt.skinwalker.sound.ModSounds.SKINWALKER_SOUND3.get(), 0.7F,
                        1.0F);
                return;
            default:
                System.out.println("Unexpected number generated.");
                return;
        }
    }

    public void transform(@NotNull Entity entity) {
        Level level = mob.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        int count = 60;
        for (int i = 0; i < count; i++) {
            Random random = new Random();
            double offsetX = random.nextGaussian() * 0.02D;
            double offsetY = random.nextGaussian() * 0.02D;
            double offsetZ = random.nextGaussian() * 0.02D;

            level.addParticle(ParticleTypes.SMOKE, x + offsetX, y + offsetY, z + offsetZ, 0, 0, 0);
        }
        entity.copyPosition(this.mob);
        mob.level().addFreshEntity(entity);
        mob.remove(Entity.RemovalReason.DISCARDED);
    }
}
