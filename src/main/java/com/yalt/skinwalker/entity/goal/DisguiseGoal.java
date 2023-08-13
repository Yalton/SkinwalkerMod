package com.yalt.skinwalker.entity.goal;

import com.yalt.skinwalker.entity.walker.SkinWalkerEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.UUID;
import java.util.random.RandomGenerator;

//import javax.annotation.Nullable;

public class DisguiseGoal extends Goal {

    private final SkinWalkerEntity mob;

    public DisguiseGoal(SkinWalkerEntity mob, double speed) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return false;
    }

    @Override
    public void start() {
        var target = this.findNearestAnimal(60.0D);
        this.transform(target);
    }

    @NotNull
    public Entity findNearestAnimal(double range) {
        AABB boundingBox = mob.getBoundingBox().inflate(range);
        UUID mobUUID = mob.getUUID();

        return mob.level.getEntitiesOfClass(
                        Animal.class,
                        boundingBox,
                        entity -> !entity.getUUID().equals(mobUUID)) // Compare UUIDs
                .stream()
                .min(Comparator.comparingDouble(mob::distanceToSqr))
                .orElse(null);
    }

    public void transform(Entity entity) {
        Level level = mob.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        int count = 60;
        for (int i = 0; i < count; i++) {
            RandomGenerator random = null;
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
