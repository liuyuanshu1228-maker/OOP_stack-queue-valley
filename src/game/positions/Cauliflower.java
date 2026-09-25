package game.positions;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.Chance;
import game.actions.WaterCauliflowerAction;
import game.items.CauliflowerItem;

/**
 * A cauliflower crop. Watering it may turn the surrounding ground into
 * puddles, and it can only be harvested once it has been watered enough
 * times.
 *
 * @see WaterCauliflowerAction
 */
public class Cauliflower extends Crop {

    /** Stamina consumed by a successful Cauliflower harvest. */
    private static final int HARVEST_STAMINA_COST = 3;

    /** Number of waterings required before this crop can be harvested. */
    private static final int REQUIRED_WATERINGS = 5;

    /** Chance (out of 100) that a ready harvest actually yields a cauliflower. */
    private static final int SUCCESS_CHANCE_PERCENT = 50;

    /**
     * Constructor.
     */
    public Cauliflower() {
        super('C', "Cauliflower");
    }

    /**
     * Offers a watering action, on top of the harvesting action inherited
     * from {@link Crop}, to an actor standing on or next to this crop.
     *
     * @see Crop#allowableActions(Actor, Location, String)
     * @param actor the actor acting
     * @param location the current location of this crop
     * @param direction the direction of this crop from the actor
     * @return the allowable actions for this crop
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        actions.add(new WaterCauliflowerAction(this, location));
        return actions;
    }

    /**
     * @return the stamina cost of harvesting a cauliflower
     */
    @Override
    public int getHarvestStaminaCost() {
        return HARVEST_STAMINA_COST;
    }

    /**
     * @return true once the cauliflower has been watered at least
     * {@value #REQUIRED_WATERINGS} times
     */
    @Override
    public boolean canHarvest() {
        return getWateringCount() >= REQUIRED_WATERINGS;
    }

    /**
     * @param actor the actor attempting to harvest the cauliflower
     * @return a description of why the harvest failed
     */
    @Override
    public String cannotHarvestMessage(Actor actor) {
        return "The " + this + " is not ready to be harvested yet.";
    }

    /**
     * Removes the cauliflower from the ground, then rolls the chance for the
     * harvest to actually yield a cauliflower.
     *
     * @param actor the actor performing the harvest
     * @param location the location of the crop being harvested
     * @return a description of the outcome
     */
    @Override
    public String harvest(Actor actor, Location location) {
        removeCrop(location);

        if (Chance.rollPercent(SUCCESS_CHANCE_PERCENT)) {
            actor.getInventory().add(new CauliflowerItem());
            return actor + " harvests the " + this + " and gets a cauliflower.";
        }

        return actor + " harvests the " + this + ", but it crumbles apart.";
    }
}
