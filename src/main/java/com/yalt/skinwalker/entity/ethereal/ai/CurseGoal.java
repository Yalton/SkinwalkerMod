package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ethereal.Ethereal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Random;

public class CurseGoal extends EtherealGoal {
    private static final int COOLDOWN_DURATION = 1 * 60 * 1000; // 5 minutes in milliseconds
    private long lastUsageTime = 0;
    private static final int CURSE_COST = -2;
    private static final int BAD_LUCK_DURATION = 6000;
    private static final int DAMAGE_BOOST_DURATION = 6000;
    private static final int CROP_ROT_RANGE = 5;
    private static final int DECREASE_HEALTH_DURATION = 60; // Duration in ticks, 20 ticks = 1 second
    private static final int BLIND_PLAYER_DURATION = 400; // 20 seconds
    private static final int SLOW_PLAYER_DURATION = 1200; // 1 minute
    private static final int MAKE_PLAYER_HUNGRY_DURATION = 2400; // 2 minutes
    private static final int RANDOM_TELEPORT_RANGE = 10; // Range for random teleportation
    private static final int DISTORT_VISION_DURATION = 400; // 20 seconds
    public boolean usedAbility = false; 


    public CurseGoal(Ethereal ethereal) {
        super(ethereal);
    }

    @Override
    public boolean canUse() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUsageTime < COOLDOWN_DURATION && !usedAbility) {
            return false;
        }
        return super.canUse() && ethereal.hasPossessedEntity && ethereal.hasTarget();
    }

    @Override
    public void start() {
        this.usedAbility = false; 
        Random random = new Random();
        int randomNumber = random.nextInt(10) + 1; // Increase the range to include the new methods

        switch (randomNumber) {
            case 1 -> {
                this.usedAbility = this.badLuck();

                return;
            }
            case 2 -> {
                 this.usedAbility = this.buffEnemy();
                return;
            }
            case 3 -> {
                this.usedAbility = this.rotCrops();
                return;
            }
            case 4 -> {
                this.usedAbility = this.replaceMilkWithWater();
                return;
            }
            case 5 -> {
                this.usedAbility = this.decreaseHealth();
                return;
            }
            case 6 -> {
                this.usedAbility = this.blindPlayer();
                return;
            }
            case 7 -> {
                this.usedAbility = this.distortPlayerVision();
                return;
            }
            case 8 -> {
                this.usedAbility = this.slowPlayer();
                return;
            }
            case 9 -> {
                this.usedAbility = this.makePlayerHungry();
                return;
            }
            case 10 -> {
                this.usedAbility = this.teleportPlayer();
                return;
            }
        }
        if (this.usedAbility == true)
        {
            lastUsageTime = System.currentTimeMillis();
            ethereal.updateBudget(CURSE_COST);
            this.playNoise();
        }
    }

    public boolean badLuck() {
        System.out.println("Applying Bad Luck");
        Player player = this.ethereal.getTarget();
        if (player != null) {
            player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, BAD_LUCK_DURATION)); // Apply bad luck for 5
                                                                                           // minutes
            return true;
        }
        return false;
    }

    public boolean buffEnemy() {
        System.out.println("Buffing Enemies");
        List<Monster> enemies = this.ethereal.level().getEntitiesOfClass(Monster.class,
                this.ethereal.getBoundingBox().inflate(10));
        if (!enemies.isEmpty()) {
            for (Monster enemy : enemies) {
                enemy.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DAMAGE_BOOST_DURATION)); // Boost damage
                                                                                                        // for 5 minutes
            }
            return true;
        }
        return false;
    }

    public boolean rotCrops() {
        System.out.println("Rotting Crops");
        for (int dx = -CROP_ROT_RANGE; dx <= CROP_ROT_RANGE; dx++) {
            for (int dz = -CROP_ROT_RANGE; dz <= CROP_ROT_RANGE; dz++) {
                BlockPos pos = this.ethereal.blockPosition().offset(dx, 0, dz);
                BlockState state = this.ethereal.level().getBlockState(pos);
                if (state.getBlock() instanceof CropBlock && state.getValue(CropBlock.AGE) > 1) {
                    this.ethereal.level().setBlock(pos,
                            state.setValue(CropBlock.AGE, state.getValue(CropBlock.AGE) - 1), 2);
                }
            }
        }
        return true;
    }

    public boolean decreaseHealth() {
        Player player = this.ethereal.getTarget();
        if (player != null) {
            player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, DECREASE_HEALTH_DURATION, -2)); // Decrease
                                                                                                            // health by
                                                                                                            // 1 heart
                                                                                                            // for 1
                                                                                                            // minute
            return true;
        }
        return false;
    }

    public boolean blindPlayer() {
        Player player = this.ethereal.getTarget();
        if (player != null) {
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, BLIND_PLAYER_DURATION)); // Blind player for 20
                                                                                                  // Seconds
            return true;
        }
        return false;
    }

    public boolean distortPlayerVision() {
        System.out.println("Distorting Player Vision");
        Player player = this.ethereal.getTarget();
        if (player != null) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, DISTORT_VISION_DURATION)); // Apply nausea for
                                                                                                    // 20 seconds
            return true;
        }
        return false;
    }

    public boolean slowPlayer() {
        Player player = this.ethereal.getTarget();
        if (player != null) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_PLAYER_DURATION)); // Slow player
                                                                                                         // for 1 minute
            return true;
        }
        return false;
    }

    public boolean makePlayerHungry() {
        Player player = this.ethereal.getTarget();
        if (player != null) {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, MAKE_PLAYER_HUNGRY_DURATION)); // Make player
                                                                                                     // hungry for 2
                                                                                                     // minutes
            return true;
        }
        return false;
    }

    public boolean teleportPlayer() {
        Player player = this.ethereal.getTarget();
        if (player != null) {
            Random random = new Random();
            double x = player.getX() + (random.nextBoolean() ? 1 : -1) * RANDOM_TELEPORT_RANGE;
            double y = player.getY();
            double z = player.getZ() + (random.nextBoolean() ? 1 : -1) * RANDOM_TELEPORT_RANGE;
            player.teleportTo(x, y, z);
            return true;
        }
        return false;
    }

    public boolean replaceMilkWithWater() {
        System.out.println("Replacing Milk with Water");
        Player player = this.ethereal.getTarget();
        if (player != null) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                // Check if the item is a milk bucket
                if (stack.getItem() == Items.MILK_BUCKET) {
                    // Replace with water bucket
                    player.getInventory().setItem(i, new ItemStack(Items.WATER_BUCKET));
                    return true;
                }
            }
        }
        return false;
    }
}
