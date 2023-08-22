package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ethereal.Ethereal;
import net.minecraft.world.entity.Entity;

public class PossessGoal extends EtherealGoal {
    private static final int POSSESS_COST = -2;
    private static final int UNPOSSESS_COST = 1;

    public PossessGoal(Ethereal ethereal) {
        super(ethereal);
    }

    @Override
    public boolean canUse() {
        return ethereal.hasBudget() && (ethereal.getPossessedEntity() == null || ethereal.hasBetterTarget());
    }

    @Override
    public void start() {
        super.start();


        // Unpossess current entity if Ethereal has a better target
        if (ethereal.getPossessedEntity() != null && ethereal.hasBetterTarget()) {
            ethereal.unpossess();
            ethereal.updateBudget(UNPOSSESS_COST);
        }

        // Try to possess a new target
        Entity target = ethereal;
        if (target != null) {
            ethereal.possess(target);
            ethereal.updateBudget(POSSESS_COST);
        }
    }
}