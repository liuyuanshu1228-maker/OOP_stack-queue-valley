package game.positions;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * A class representing a puddle on the ground, produced by watering Dirt
 * or by a Cauliflower crop soaking its surroundings. A puddle is capable of
 * holding a charge.
 */
public class Puddle extends Ground implements Chargeable {

    /**
     * Constructor.
     */
    public Puddle() {
        super('~', "Puddle");
    }

    /**
     * Returns the Ground that results from charging this Puddle.
     *
     * @return a new ChargedPuddle
     */
    @Override
    public Ground getChargedResult() {
        return new ChargedPuddle();
    }
}