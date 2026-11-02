package game.positions;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * Capability interface for Ground that is capable of holding a charge.
 * Implementing classes define what the Ground becomes once it is charged.
 */
public interface Chargeable {

    /**
     * Returns the Ground that results from charging this Ground.
     *
     * @return the charged version of this Ground
     */
    public Ground getChargedResult();
}
