package game.wanderer;

import edu.monash.fit2099.engine.positions.Location;

/**
 * What the Wanderer does to the farm because of the weather where it stands
 * right now, such as lighting a campfire in the cold.
 * <p>
 * Each reaction is made with the weather reading that chose it, and uses that
 * reading's values to decide how strong it is. The Wanderer depends only on
 * this interface, so a new reaction needs no change to the Wanderer.
 */
public interface WeatherReaction {

    /**
     * Changes the farm around the Wanderer.
     *
     * @param here the tile the Wanderer stands on
     * @return a description of what happened, or an empty string if nothing
     * happened
     */
    public String reactAround(Location here);
}
