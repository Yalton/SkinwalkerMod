package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ethereal.Ethereal;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

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

//    public void rotCrops() {
//        int range = 5; // Example range
//        for (int dx = -range; dx <= range; dx++) {
//            for (int dz = -range; dz <= range; dz++) {
//                BlockPos pos = this.ethereal.blockPosition().offset(dx, 0, dz);
//                BlockState state = this.ethereal.level().getBlockState(pos);
//                Block block = state.getBlock();
//                if (block instanceof CropBlock && state.getValue(CropBlock.AGE) > 1) {
//                    level.setBlock(pos, state.setValue(CropBlock.AGE, state.getValue(CropBlock.AGE) - 1), 2);
//                }
//            }
//        }
//    }
}
