package com.yalt.skinwalker.entity.ethereal.ai;

import net.minecraft.world.entity.ai.goal.Goal;

public abstract class EtherealGoal extends Goal {
    @Override
    public boolean canUse() {
        return false;
    }
}
