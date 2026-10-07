package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.statistics.StatisticAdjuster;

/**
 * An Action that allows an Actor to harvest a crop, either while standing on it
 * or from an adjacent tile, provided the crop is ready ({@link Harvestable#canHarvest()})
 * and the Actor has enough stamina.
 */
public class HarvestAction extends Action {

    /** The map location of the crop to be harvested. */
    private final Location location;

    /**
     * Constructor.
     *
     * @param location the Location of the crop to be harvested
     */
    public HarvestAction(Location location) {
        this.location = location;
    }

    /**
     * Performs the harvesting attempt. Fails with a crop-specific message if the
     * crop isn't ready yet. Otherwise, if the Actor has enough stamina, deducts
     * it and delegates to the crop's Harvestable effect; if not, fails without
     * any side effects.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Harvestable harvestable = location.getGroundAs(Harvestable.class);

        if (!harvestable.canHarvest()) {
            return harvestable.cannotHarvestMessage(actor);
        }

        int cost = harvestable.getHarvestStaminaCost();
        if (!new StatisticAdjuster(actor).trySpend(ActorStatistics.STAMINA, cost)) {
            return actor + " is too tired to harvest the " + location.getGround() + ".";
        }

        return harvestable.harvest(actor, location);
    }

    /**
     * Describes what this Action will do if chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " harvests the " + location.getGround() + ".";
    }
}
