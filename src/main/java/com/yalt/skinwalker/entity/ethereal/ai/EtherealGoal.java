package com.yalt.skinwalker.entity.ethereal.ai;

import java.util.Random;

import com.yalt.skinwalker.entity.ethereal.Ethereal;
import com.yalt.skinwalker.sound.ModSounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class EtherealGoal extends Goal {
    protected final Ethereal ethereal;
    private long lastNoiseTime;

    public EtherealGoal(Ethereal ethereal) {
        this.ethereal = ethereal;
        this.lastNoiseTime = 0;
    }

    @Override
    public boolean canUse() {
        return ethereal.hasBudget();
    }

    public void playNoise() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastNoiseTime >= 5000) {
            Random random = new Random();
            int randomNumber = random.nextInt(7) + 1;

            switch (randomNumber) {
                case 1:
                    this.ethereal.playEntitySound((SoundEvent) ModSounds.SKINWALKER_TALKING1.get(), 0.7F, 1.0F);
                    break;
                case 2:
                    this.ethereal.playEntitySound(
                            (SoundEvent) com.yalt.skinwalker.sound.ModSounds.SKINWALKER_TALKING2.get(), 0.7F, 1.0F);
                    break;
                case 3:
                    this.ethereal.playEntitySound(
                            (SoundEvent) com.yalt.skinwalker.sound.ModSounds.SKINWALKER_TALKING3.get(), 0.7F, 1.0F);
                    break;
                case 4:
                    this.ethereal.playEntitySound(SoundEvents.ENDERMAN_SCREAM, 0.7F, 1.0F);
                    break;
                case 5:
                    this.ethereal.playEntitySound(SoundEvents.DROWNED_AMBIENT, 0.7F, 1.0F);
                    break;
                case 6:
                    this.ethereal.playEntitySound(SoundEvents.WARDEN_ROAR, 0.7F, 1.0F);
                    break;
                case 7:
                    this.ethereal.playEntitySound(SoundEvents.SCULK_SHRIEKER_SHRIEK, 0.7F, 1.0F);
                    break;
                default:
                    System.out.println("Unexpected number generated.");
                    break;
            }

            lastNoiseTime = currentTime;
        }
    }
}
