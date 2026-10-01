package game.positions;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.WaterCropAction;
import game.items.CauliflowerItem;

import java.util.Random;

/**
 * A cauliflower crop. Watering it may turn the surrounding ground into
 * puddles, and it can only be harvested once it has been watered enough
 * times.
 */
public class Cauliflower extends Crop implements CropWaterable {

    /** Stamina consumed by a successful Cauliflower harvest. */
    private static final int HARVEST_STAMINA_COST = 3;

    /** Stamina consumed by a successful Cauliflower watering. */
    private static final int WATERING_STAMINA_COST = 2;

    /** Number of waterings required before this crop can be harvested. */
    private static final int REQUIRED_WATERINGS = 5;

    /** Chance (out of 100) that a ready harvest actually yields a cauliflower. */
    private static final int SUCCESS_CHANCE_PERCENT = 50;

    /** Chance (out of 100) that watering turns the surrounding ground into puddles. */
    private static final int PUDDLE_CHANCE_PERCENT = 50;

    private final Random random = new Random();

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
        actions.add(new WaterCropAction(location));
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

        if (random.nextInt(100) < SUCCESS_CHANCE_PERCENT) {
            actor.getInventory().add(new CauliflowerItem());
            return actor + " harvests the " + this + " and gets a cauliflower.";
        }

        return actor + " harvests the " + this + ", but it crumbles apart.";
    }

    /**
     * @return the stamina cost of watering a cauliflower
     */
    @Override
    public int getWateringStaminaCost() {
        return WATERING_STAMINA_COST;
    }

    /**
     * Rolls the chance to turn the adjacent, non-crop ground tiles into
     * puddles.
     *
     * @param actor the actor watering the cauliflower
     * @param location the location of the cauliflower being watered
     * @return a description of what happened
     */
    @Override
    public String water(Actor actor, Location location) {
        String message = actor + " waters the " + this + ".";

        if (random.nextInt(100) < PUDDLE_CHANCE_PERCENT) {
            for (Location adjacentLocation : location.getNearbyLocations(1)) {
                if (adjacentLocation.getGroundAs(Crop.class) == null) {
                    adjacentLocation.setGround(new Puddle());
                }
            }
            message += " The surrounding ground turns into puddles.";
        }

        return message;
    }
}
