package game;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * Abstract base class shared by actions that act on a {@link Crop} at a cost
 * of stamina, such as watering or harvesting.
 * <p>
 * This class is intentionally not public: only {@link WaterCropAction} and
 * {@link HarvestCropAction} extend it directly, and every crop-specific
 * action goes through one of those two instead. Their inherited protected
 * members remain usable from crop-specific subclasses in other packages
 * (e.g. {@code game.parsnip}) regardless of this class's own visibility.
 */
abstract class CropAction extends Action {

    /**
     * The crop being acted upon.
     */
    protected final Crop crop;

    /**
     * The location of the crop being acted upon.
     */
    protected final Location cropLocation;

    /**
     * Constructor.
     *
     * @param crop the crop to act upon
     * @param cropLocation the location of the crop to act upon
     */
    protected CropAction(Crop crop, Location cropLocation) {
        this.crop = crop;
        this.cropLocation = cropLocation;
    }

    /**
     * The amount of stamina it costs to perform this action on the crop.
     *
     * @return the stamina cost of this action
     */
    protected abstract int getStaminaCost();

    /**
     * @param actor the actor performing the action
     * @return true if the actor has enough stamina to perform this action
     */
    protected final boolean hasEnoughStamina(Actor actor) {
        return actor.getStatistic(ActorStatistics.STAMINA) >= getStaminaCost();
    }

    /**
     * Deducts the stamina cost of this action from the actor.
     *
     * @param actor the actor performing the action
     */
    protected final void deductStamina(Actor actor) {
        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, getStaminaCost());
    }

    /**
     * Removes the crop from the ground, turning its tile back into dirt.
     */
    protected final void destroyCrop() {
        crop.removeCrop(cropLocation);
    }
}
