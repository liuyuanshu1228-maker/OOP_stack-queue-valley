package game.parsnip;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.Crop;
import game.LocationUtils;

import java.util.Random;

/**
 * A parsnip crop. It can be watered, which heals the farmer but risks
 * ruining the plant, and it can be harvested at any time for a Parsnip,
 * with a chance of a bonus Parsnip appearing nearby.
 *
 * @see WaterParsnipAction
 */
public class Parsnip extends Crop {

    /** Stamina consumed by a successful Parsnip harvest. */
    private static final int HARVEST_STAMINA_COST = 1;

    /** Chance (0.0 to 1.0) that a second Parsnip spawns on an adjacent tile. */
    private static final double BONUS_SPAWN_CHANCE = 0.5;

    private final Random random = new Random();

    /**
     * Constructor.
     */
    public Parsnip() {
        super('P', "Parsnip");
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
        actions.add(new WaterParsnipAction(this, location));
        return actions;
    }

    /**
     * @return the stamina cost of harvesting a parsnip
     */
    @Override
    public int getHarvestStaminaCost() {
        return HARVEST_STAMINA_COST;
    }

    /**
     * Adds one Parsnip to the actor's inventory, replaces the ground with
     * dirt, and has a 50% chance of placing a second Parsnip on a tile
     * adjacent to this crop's former location.
     *
     * @param actor the actor performing the harvest
     * @param location the location of the crop being harvested
     * @return a description of the outcome
     */
    @Override
    public String harvest(Actor actor, Location location) {
        actor.getInventory().add(new ParsnipItem());
        removeCrop(location);

        if (random.nextDouble() < BONUS_SPAWN_CHANCE) {
            LocationUtils.dropItemNearby(location, new ParsnipItem(), random);
        }

        return actor + " harvests the Parsnip crop.";
    }
}
