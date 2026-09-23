package game.ground;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * Capability interface for Ground that can be tilled by an Actor carrying a Hoe.
 * Implementing classes define what Ground results from being tilled.
 */
public interface Tillable {

    /**
     * Returns the Ground that results from tilling this Ground.
     *
     * @return the new Ground after tilling
     */
    Ground getTillResult();
}
