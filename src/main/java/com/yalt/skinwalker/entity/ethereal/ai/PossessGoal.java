package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ethereal.Ethereal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class PossessGoal extends EtherealGoal {
    private static final int POSSESS_COST = -2;
    private static final int UNPOSSESS_COST = 1;
    Player nearestPlayer;

    public PossessGoal(Ethereal ethereal) {
        super(ethereal);
    }


    @Override
    public boolean canUse() {
        // If Ethereal has budget and (Ethereal has no possessed entity or Ethereal has a better target)
        if (ethereal.hasBudget() && (ethereal.getPossessedEntity() == null || ethereal.hasBetterTarget())) {
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
        // Unpossess current entity if Ethereal has a better target
        if (ethereal.getPossessedEntity() != null && ethereal.hasBetterTarget()) {
            System.out.println("Unpossessing Entity" + ethereal.getPossessedEntity().getDisplayName().getString());
            ethereal.unpossess();
            ethereal.updateBudget(UNPOSSESS_COST);
            ethereal.hasPossessedEntity = false;
            ethereal.updatedPossessedEntityGoals = false;
        }

        // Try to possess a new target
        Mob target = ethereal.getNearestAnimal();
        if (target != null && !ethereal.hasPossessedEntity) {
            System.out.println("Possessing Entity: " + target.getDisplayName().getString());
            ethereal.possess(target);
            ethereal.updateBudget(POSSESS_COST);
            ethereal.hasPossessedEntity = true;
            ethereal.updatedPossessedEntityGoals = false;
        }

        if (ethereal.hasPossessedEntity) {
            ethereal.updateGoalsIfNeeded();
        }
    }

}