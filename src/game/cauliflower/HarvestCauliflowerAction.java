package game.cauliflower;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.Chance;
import game.HarvestCropAction;

/**
 * The action of harvesting a {@link Cauliflower}.
 * <p>
 * The cauliflower must have been watered enough times before it can be
 * harvested at all. Once ready, harvesting always removes the crop, but
 * only sometimes yields a cauliflower.
 */
class HarvestCauliflowerAction extends HarvestCropAction {

    private static final int STAMINA_COST = 3;
    private static final int REQUIRED_WATERINGS = 5;
    private static final int SUCCESS_CHANCE_PERCENT = 50;

    /**
     * Constructor.
     *
     * @param crop the cauliflower to be harvested
     * @param cropLocation the location of the cauliflower to be harvested
     */
    HarvestCauliflowerAction(Cauliflower crop, Location cropLocation) {
        super(crop, cropLocation);
    }

    /**
     * @return the stamina cost of harvesting a cauliflower
     */
    @Override
    protected int getStaminaCost() {
        return STAMINA_COST;
    }

    /**
     * @return true once the cauliflower has been watered at least
     * {@value #REQUIRED_WATERINGS} times
     */
    @Override
    protected boolean canHarvest() {
        return crop.getWateringCount() >= REQUIRED_WATERINGS;
    }

    /**
     * @param actor the actor attempting to harvest the cauliflower
     * @return a description of why the harvest failed
     */
    @Override
    protected String cannotHarvestMessage(Actor actor) {
        return "The " + crop + " is not ready to be harvested yet.";
    }

    /**
     * Removes the cauliflower from the ground, then rolls the chance for the
     * harvest to actually yield a cauliflower.
     *
     * @param actor the actor harvesting the cauliflower
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    protected String applyHarvestEffect(Actor actor, GameMap map) {
        destroyCrop();

        if (Chance.rollPercent(SUCCESS_CHANCE_PERCENT)) {
            actor.getInventory().add(new CauliflowerItem());
            return actor + " harvests the " + crop + " and gets a cauliflower.";
        }

        return actor + " harvests the " + crop + ", but it crumbles apart.";
    }
}
