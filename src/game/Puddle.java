package game;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * A class representing a puddle on the ground, produced by watering Dirt
 * or by a Cauliflower crop soaking its surroundings.
 */
public class Puddle extends Ground {

    /**
     * Constructor.
     */
    public Puddle() {
        super('~', "Puddle");
    }
}
