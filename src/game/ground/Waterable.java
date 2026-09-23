package game.ground;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * Capability interface for Ground that can be watered by an Actor carrying a Watering Can.
 * Implementing classes define what Ground results from being watered.
 */
public interface Waterable {

    /**
     * Returns the Ground that results from watering this Ground.
     *
     * @return the new Ground after watering
     */
    Ground getWaterResult();
}
