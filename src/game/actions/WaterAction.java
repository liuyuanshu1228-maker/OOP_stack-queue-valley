package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.tools.WateringTool;
import game.positions.Waterable;
import game.statistics.StatisticUtils;

import java.util.List;

/**
 * An Action that allows an Actor to water the Ground at their current Location,
 * provided they are carrying a Watering Can and have enough stamina.
 * The Ground must implement Waterable to define what it becomes once watered.
 * <p>
 * This is unrelated to watering a mature {@link game.crops.Crop}: this action turns
 * one type of Ground into another (e.g. Dirt into a Puddle), whereas watering a
 * crop nurtures it in place and is handled by {@link game.actions.WaterCropAction}.
 */
public class WaterAction extends Action {

    /** The map location whose ground is to be watered. */
    private final Location location;

    /**
     * Constructor.
     *
     * @param location the Location whose Ground is to be watered
     */
    public WaterAction(Location location) {
        this.location = location;
    }

    /**
     * Performs the watering attempt. If the Actor is carrying a watering tool
     * and has enough stamina for that tool's cost, waters the Ground and
     * deducts stamina; otherwise, fails without any side effects.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Waterable waterable = location.getGroundAs(Waterable.class);
        List<WateringTool> tools = actor.getInventory().getItemsAs(WateringTool.class);

        if (tools.isEmpty()) {
            return actor + " has no watering can to water the ground.";
        }
        if (waterable == null) {
            return actor + " cannot water this ground.";
        }

        if (!StatisticUtils.trySpend(actor, ActorStatistics.STAMINA, tools.get(0).getStaminaCost())) {
            return actor + " is too tired to water the ground.";
        }

        location.setGround(waterable.getWaterResult());
        return actor + " waters the ground.";
    }

    /**
     * Describes what this Action will do if chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " waters the ground here.";
    }
}
