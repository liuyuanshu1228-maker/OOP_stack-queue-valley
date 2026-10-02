package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.items.tools.WateringTool;
import game.crops.Crop;
import game.crops.CropWaterable;
import game.statistics.StatisticUtils;

/**
 * An Action that allows an Actor to water a {@link Crop}, provided they are
 * carrying a watering tool and have enough stamina for that crop's cost.
 * <p>
 * This is unrelated to A1's ground-level {@link WaterAction}, which turns
 * raw Dirt into a Puddle; this class nurtures an already-mature crop in
 * place.
 */
public class WaterCropAction extends Action {

    private final Location cropLocation;

    /**
     * Constructor.
     *
     * @param cropLocation the Location of the crop to be watered
     */
    public WaterCropAction(Location cropLocation) {
        this.cropLocation = cropLocation;
    }

    /**
     * Waters the crop: requires the actor to carry a {@link WateringTool} and
     * to have enough stamina for the crop-specific cost. On success, the
     * stamina is deducted, the watering is recorded on the crop, and the
     * crop-specific effect is applied.
     *
     * @see Action#execute(Actor, GameMap)
     * @param actor the actor watering the crop
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        CropWaterable waterable = cropLocation.getGroundAs(CropWaterable.class);

        if (actor.getInventory().getItemsAs(WateringTool.class).isEmpty()) {
            return actor + " has no watering tool to water the " + cropLocation.getGround() + ".";
        }

        int cost = waterable.getWateringStaminaCost();
        if (!StatisticUtils.hasEnough(actor, ActorStatistics.STAMINA, cost)) {
            return actor + " is too tired to water the " + cropLocation.getGround() + ".";
        }

        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, cost);
        cropLocation.getGroundAs(Crop.class).recordWatering();

        return waterable.water(actor, cropLocation);
    }

    /**
     * Describes this action in a format suitable for displaying in the menu.
     *
     * @see Action#menuDescription(Actor)
     * @param actor the actor performing the action
     * @return a string, e.g. "Farmer Bob waters the Parsnip"
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " waters the " + cropLocation.getGround();
    }
}
