package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ethereal.Ethereal;

public class InvokeGoal extends EtherealGoal {
    public InvokeGoal(Ethereal ethereal) {
        super(ethereal);
    }

    @Override
    public boolean canUse() {
        return ethereal.getPossessedEntity() != null; // Add any other conditions
    }

    @Override
    public void start() {
        super.start();
        ethereal.performAction(); // Calling the specific action method
    }
}
