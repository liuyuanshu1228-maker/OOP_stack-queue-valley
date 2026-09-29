package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.items.WateringTool;
import game.positions.Waterable;
import game.statistics.StatisticUtils;

import java.util.List;

/**
 * An Action that allows an Actor to water the Ground at their current Location,
 * provided they are carrying a Watering Can and have enough stamina.
 * The Ground must implement Waterable to define what it becomes once watered.
 * <p>
 * This is unrelated to watering a mature {@link game.positions.Crop}: this action turns
 * one type of Ground into another (e.g. Dirt into a Puddle), whereas watering a
 * crop nurtures it in place and is handled by {@link game.actions.WaterCropAction}.
 */
public class WaterAction extends Action {

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

        if (waterable == null || tools.isEmpty()) {
            return actor + " is either too tired to water the ground or don't have a Watering Can to water the ground.";
        }

        int staminaCost = tools.get(0).getStaminaCost();
        if (!StatisticUtils.hasEnough(actor, ActorStatistics.STAMINA, staminaCost)) {
            return actor + " is either too tired to water the ground or don't have a Watering Can to water the ground.";
        }

        location.setGround(waterable.getWaterResult());
        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, staminaCost);
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
