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

public class CurseGoal extends EtherealGoal {
    public CurseGoal(Ethereal ethereal) {
        super(ethereal);
    }

    public void badLuck() {
        Player player = this.ethereal.level().getNearestPlayer(ethereal, 10); // Find a player within 10 blocks
        if (player != null) {
            player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 6000)); // Apply bad luck for 5 minutes
        }

    }

    public void buffEnemy() {
        List<Monster> enemies = this.ethereal.level().getEntitiesOfClass(Monster.class, this.ethereal.getBoundingBox().inflate(10));
        for (Monster enemy : enemies) {
            enemy.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 6000)); // Boost damage for 5 minutes
        }
    }

    public void rotCrops() {
        int range = 5;
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                BlockPos pos = this.ethereal.blockPosition().offset(dx, 0, dz);
                BlockState state = this.ethereal.level().getBlockState(pos);
                if (state.getBlock() instanceof CropBlock && state.getValue(CropBlock.AGE) > 1) {
                    this.ethereal.level().setBlock(pos, state.setValue(CropBlock.AGE, state.getValue(CropBlock.AGE) - 1), 2);
                }
            }
        }
    }

    public void replaceMilkWithWater() {
        Player player = this.ethereal.level().getNearestPlayer(ethereal, 10); // Find a player within 10 blocks

        if (player != null) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);

                // Check if the item is a milk bucket
                if (stack.getItem() == Items.MILK_BUCKET) {
                    // Replace with water bucket
                    player.getInventory().setItem(i, new ItemStack(Items.WATER_BUCKET));
                }
            }
        }
    }
}
