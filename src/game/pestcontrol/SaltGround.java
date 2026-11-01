package game.pestcontrol;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import game.pests.Infestation;
import game.positions.TemporaryGround;

/**
 * Salted farm ground. Pests cannot enter it, and nothing can be planted on it
 * while the salt lasts. It turns back into the ground it covered after a few
 * turns.
 */
public class SaltGround extends TemporaryGround {

    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = '+';

    /** The name of this ground. */
    private static final String NAME = "Salt";

    /** How many turns the salt lasts. */
    private static final int DURATION_TURNS = 8;

    /**
     * Covers a ground with salt.
     *
     * @param originalGround the ground to restore when the salt is gone
     */
    public SaltGround(Ground originalGround) {
        super(DISPLAY_CHAR, NAME, originalGround, DURATION_TURNS);
    }

    /**
     * Keeps pests out; every other actor may enter.
     *
     * @param actor the actor trying to enter
     * @return false if the actor is a pest
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return actor.asCapability(Infestation.class).isEmpty();
    }
}
