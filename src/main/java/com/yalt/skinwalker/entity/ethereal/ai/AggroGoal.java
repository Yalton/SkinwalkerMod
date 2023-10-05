package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ModEntityTypes;
import com.yalt.skinwalker.entity.ethereal.Ethereal;
import com.yalt.skinwalker.entity.walker.SkinWalkerEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.Random;

public class AggroGoal extends EtherealGoal {

    public AggroGoal(Ethereal ethereal) {
        super(ethereal);
    }

    @Override
    public boolean canUse() {
        return sustainedDamage();
    }

    private boolean sustainedDamage() {
        if (this.ethereal.possessedEntity != null) {
            return ((Mob) this.ethereal.possessedEntity).getLastHurtByMob() != null;
        } else if (this.ethereal.possessedEntity == null) {
            return ((Mob) this.ethereal).getLastHurtByMob() != null;
        }
        return false;
    }


    @Override
    public void start() {
        if (ethereal.possessedEntity != null) {
            transformAndRemoveEntities(ethereal.possessedEntity);
            ethereal.possessedEntity.remove(Entity.RemovalReason.DISCARDED);

        } else {
            transformAndRemoveEntities(ethereal);
        }
    }

    private void transformAndRemoveEntities(Entity entityToRemove) {
        this.playNoise();
        SkinWalkerEntity skinWalker = new SkinWalkerEntity(ModEntityTypes.SKIN_WALKER.get(), this.ethereal.level());
        Level level = this.ethereal.level();
        double x = entityToRemove.getX();
        double y = entityToRemove.getY();
        double z = entityToRemove.getZ();
        spawnParticles(level, x, y, z, 60);
        skinWalker.copyPosition(entityToRemove);
        level.addFreshEntity(skinWalker);
        entityToRemove.remove(Entity.RemovalReason.DISCARDED);
        spawnParticles(level, skinWalker.getX(), skinWalker.getY(), skinWalker.getZ(), 600); // Spawn particles at the new SkinWalkerEntity's position
    }

    private void spawnParticles(Level level, double x, double y, double z, int count) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            double offsetX = random.nextGaussian() * 0.02D;
            double offsetY = random.nextGaussian() * 0.02D;
            double offsetZ = random.nextGaussian() * 0.02D;
            level.addParticle(ParticleTypes.SMOKE, x + offsetX, y + offsetY, z + offsetZ, 0, 0, 0);
        }
    }
}