package game.positions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Capability interface for Ground that can be watered by an Actor carrying a watering tool.
 * Implementing classes define what happens when they are watered: a Dirt tile turns into a
 * Puddle, and a crop records the watering and applies its own effect.
 */
public interface Waterable {

    /**
     * Applies the effect of watering this Ground. Assumes the caller has already checked that
     * the Actor has a watering tool and enough stamina. A crop must record the watering before
     * it applies its effect, because several crop rules depend on the watering count.
     *
     * @param actor the Actor watering this Ground
     * @param location the Location of the Ground being watered
     * @return a description of the outcome, to be displayed to the player
     */
    String water(Actor actor, Location location);

    /**
     * Returns the stamina cost of watering this Ground. The default is the cost of the tool
     * itself; a Ground that has its own cost overrides it and ignores the tool's cost.
     *
     * @param toolCost the stamina cost of the watering tool
     * @return the stamina cost of watering this Ground
     */
    default int getWateringStaminaCost(int toolCost) {
        return toolCost;
    }
}
