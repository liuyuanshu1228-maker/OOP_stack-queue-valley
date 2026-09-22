package game.cauliflower;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * A puddle, left behind when watering a {@link Cauliflower} soaks the
 * surrounding ground.
 */
class Puddle extends Ground {

    /**
     * Constructor.
     */
    Puddle() {
        super('~', "Puddle");
    }
}
