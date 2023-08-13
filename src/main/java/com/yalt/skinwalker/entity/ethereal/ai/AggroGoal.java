package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ModEntityTypes;
import com.yalt.skinwalker.entity.ethereal.Ethereal;
import com.yalt.skinwalker.entity.walker.SkinWalkerEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Random;

public class AggroGoal extends EtherealGoal {
    private static final int POSSESS_COST = -2;
    private static final int UNPOSSESS_COST = 1;

    public AggroGoal(Ethereal ethereal) {
        super(ethereal);
    }

    @Override
    public boolean canUse() {
        if (this.sustainedDamage()) {
            return true;
        }
        return false;
    }

    public boolean sustainedDamage() {
        if (this.ethereal.getActor() != null) {
            return this.ethereal.getActor().getLastHurtByMob() != null;
        }
        return false;
    }

    public void start() {
        transform();
    }

    public void transform() {
        SkinWalkerEntity skinWalker = new SkinWalkerEntity(ModEntityTypes.SKIN_WALKER.get(), this.ethereal.level());
        Level level = this.ethereal.level();
        double x = this.ethereal.getX();
        double y = this.ethereal.getY();
        double z = this.ethereal.getZ();
        int count = 60;
        Random random = new Random(); // Initialize Random object here
        for (int i = 0; i < count; i++) {
            double offsetX = random.nextGaussian() * 0.02D;
            double offsetY = random.nextGaussian() * 0.02D;
            double offsetZ = random.nextGaussian() * 0.02D;

            level.addParticle(ParticleTypes.SMOKE, x + offsetX, y + offsetY, z + offsetZ, 0, 0, 0);
        }
        skinWalker.copyPosition(this.ethereal);
        ethereal.level().addFreshEntity(skinWalker);
        ethereal.remove(Entity.RemovalReason.DISCARDED);
    }

}