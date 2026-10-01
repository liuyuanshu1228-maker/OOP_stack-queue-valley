package game.positions;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.WaterStrawberryAction;
import game.items.StrawberryItem;

import java.util.Random;

/**
 * A strawberry crop. It can be watered, which scatters strawberries onto
 * adjacent tiles, and it can be harvested at any time for strawberries,
 * though harvesting risks destroying the plant.
 *
 * @see WaterStrawberryAction
 */
public class Strawberry extends Crop {

    /** Stamina consumed by a successful Strawberry harvest. */
    private static final int HARVEST_STAMINA_COST = 1;

    /** Number of strawberries produced by a harvest. */
    private static final int STRAWBERRIES_PER_HARVEST = 2;

    /** Base chance (out of 100) that harvesting destroys the plant, before subtracting the watering count. */
    private static final int BASE_DESTRUCTION_CHANCE_PERCENT = 50;

    private final Random random = new Random();

    /**
     * Constructor.
     */
    public Strawberry() {
        super('S', "Strawberry");
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
        actions.add(new WaterStrawberryAction(this, location));
        return actions;
    }

    /**
     * @return the stamina cost of harvesting a strawberry
     */
    @Override
    public int getHarvestStaminaCost() {
        return HARVEST_STAMINA_COST;
    }

    /**
     * Adds two strawberries to the actor's inventory, then rolls the chance
     * for the plant to be destroyed. The chance shrinks by one percentage
     * point for every time the strawberry has been watered.
     *
     * @param actor the actor performing the harvest
     * @param location the location of the crop being harvested
     * @return a description of the outcome
     */
    @Override
    public String harvest(Actor actor, Location location) {
        for (int i = 0; i < STRAWBERRIES_PER_HARVEST; i++) {
            actor.getInventory().add(StrawberryItem.createRandom());
        }

        String message = actor + " harvests the " + this + " and picks up 2 strawberries.";

        int destructionChancePercent = BASE_DESTRUCTION_CHANCE_PERCENT - getWateringCount();
        if (random.nextInt(100) < destructionChancePercent) {
            removeCrop(location);
            message += " The " + this + " couldn't withstand the harvest and was destroyed.";
        }

        return message;
    }
}
