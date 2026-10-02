package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Capability interface for Ground that can be harvested by an Actor,
 * either while standing on it or from an adjacent tile.
 * Implementing classes define the effect of a successful harvest:
 * item production, ground replacement, and any crop-specific side effects.
 * Stamina checking is handled by the calling Action, not by this method.
 */
public interface Harvestable {

    /**
     * Returns the stamina cost required to harvest this crop.
     *
     * @return the stamina cost
     */
    int getHarvestStaminaCost();

    /**
     * Applies the effect of a successful harvest to the given Actor and Location.
     * Assumes stamina has already been verified as sufficient by the caller.
     *
     * @param actor    the Actor performing the harvest
     * @param location the Location of the crop being harvested
     * @return a description of the outcome, to be displayed to the player
     */
    String harvest(Actor actor, Location location);

    /**
     * Whether this crop is currently allowed to be harvested. Defaults to
     * true; override for crops that impose a precondition, such as
     * requiring a minimum number of waterings first.
     *
     * @return true if the crop can be harvested right now
     */
    default boolean canHarvest() {
        return true;
    }

    /**
     * The message returned when {@link #canHarvest()} is false. Override
     * alongside {@link #canHarvest()} to give a more specific reason.
     *
     * @param actor the Actor attempting to harvest the crop
     * @return a description of why the harvest failed
     */
    default String cannotHarvestMessage(Actor actor) {
        return actor + " cannot harvest the " + this + " yet.";
    }
}
