package game;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.items.WateringTool;

/**
 * Abstract base class for actions that water a {@link Crop}.
 * <p>
 * Every crop requires a watering tool and a crop-specific amount of stamina
 * to be watered. This class takes care of that common bookkeeping (checking
 * for a watering tool, checking/deducting stamina, and recording the
 * watering) and delegates the crop-specific effect of watering to
 * subclasses.
 * <p>
 * This is unrelated to A1's ground-level {@link game.actions.WaterAction},
 * which turns raw Dirt into a Puddle; this class nurtures an already-mature
 * crop in place.
 */
public abstract class WaterCropAction extends Action {

    /**
     * The crop being watered.
     */
    protected final Crop crop;

    /**
     * The location of the crop being watered.
     */
    protected final Location cropLocation;

    /**
     * Constructor.
     *
     * @param crop the crop to be watered
     * @param cropLocation the location of the crop to be watered
     */
    public WaterCropAction(Crop crop, Location cropLocation) {
        this.crop = crop;
        this.cropLocation = cropLocation;
    }

    /**
     * The amount of stamina it costs to water this crop.
     *
     * @return the stamina cost of watering this crop
     */
    protected abstract int getStaminaCost();

    /**
     * Applies the crop-specific effect of being watered. Called only after
     * the watering tool and stamina checks have passed, and after the
     * stamina has been deducted and the watering has been recorded.
     *
     * @param actor the actor watering the crop
     * @param map the map the actor is on
     * @return a description of what happened
     */
    protected abstract String applyWateringEffect(Actor actor, GameMap map);

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
        if (actor.getInventory().getItemsAs(WateringTool.class).isEmpty()) {
            return actor + " has no watering tool to water the " + crop + ".";
        }

        if (actor.getStatistic(ActorStatistics.STAMINA) < getStaminaCost()) {
            return actor + " is too tired to water the " + crop + ".";
        }

        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, getStaminaCost());
        crop.recordWatering();

        return applyWateringEffect(actor, map);
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
        return actor + " waters the " + crop;
    }

    /**
     * Removes the crop being watered from the ground, turning its tile back
     * into dirt.
     * <p>
     * {@link Crop#removeCrop(Location)} is protected and {@code crop} is only
     * known to be a {@link Crop} here, so concrete crop-specific subclasses
     * (which live in their own crop packages, not in {@code game}) go through
     * this inherited method rather than needing package-level access to
     * {@link Crop} themselves.
     */
    protected final void destroyCrop() {
        crop.removeCrop(cropLocation);
    }
}
