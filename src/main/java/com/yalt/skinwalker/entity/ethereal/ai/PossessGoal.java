package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ethereal.Ethereal;

public class PossessGoal extends EtherealGoal {
    private static final int POSSESS_COST = -2;
    private static final int UNPOSSESS_COST = 1;

    public PossessGoal(Ethereal ethereal) {
        super(ethereal);
    }

    @Override
    public boolean canUse() {
        ethereal.tryToAcquireTarget();
//        return ethereal.hasBudget();
        // TODO: rEmove.
        return false;
    }

    @Override
    public void start() {
        super.start();
//        tryToPossess();

    }

//    public void tryToPossess() {
//        var player = ethereal.tryToAcquireTarget();
//        if (player == null) {
//            return;
//        }
//
//        ethereal.teleportTo(player.getX(), player.getY(), player.getZ());
//        var animal = ethereal.getNearestAnimal();
//        if (animal != null) {
//            ethereal.updateBudget(POSSESS_COST);
//            ethereal.setActor(animal);
//        }
//    }

}
