package game.wanderer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import game.positions.TemporaryGround;

/**
 * Ground sheltered against a storm. Nothing can enter it while it lasts; it
 * reverts to the ground it covered once its duration is up.
 */
public class StormShelterGround extends TemporaryGround {

    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = 'H';
    /** The name of this ground. */
    private static final String NAME = "Storm Shelter";

    /**
     * Covers a ground with a storm shelter.
     *
     * @param originalGround the ground to restore once the shelter is gone
     * @param durationTurns how many turns the shelter lasts; at least 1
     */
    public StormShelterGround(Ground originalGround, int durationTurns) {
        super(DISPLAY_CHAR, NAME, originalGround, durationTurns);
    }

    /**
     * Keeps every actor out while the shelter stands.
     *
     * @param actor the actor trying to enter
     * @return false, always
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }
}
