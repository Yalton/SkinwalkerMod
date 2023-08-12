package com.yalt.skinwalker.entity.goal;

import com.yalt.skinwalker.entity.disguise.CowDisguiseEntity;
import com.yalt.skinwalker.entity.walker.SkinWalkerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.NotNull;

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
        var target = this.findNearestAnimal();
        this.transform(target);
    }

    @NotNull
    public Entity findNearestAnimal() {
        return new CowDisguiseEntity(EntityType.COW, mob.level());
    }

    public void transform(Entity entity) {
        entity.copyPosition(this.mob);
        mob.level().addFreshEntity(entity);
        mob.remove(Entity.RemovalReason.DISCARDED);
    }

}
