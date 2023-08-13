package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ethereal.Ethereal;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class EtherealGoal extends Goal {
    protected final Ethereal ethereal;

    public EtherealGoal(Ethereal ethereal) {
        this.ethereal = ethereal;
    }

    @Override
    public boolean canUse() {
        return ethereal.hasBudget();
    }
}
