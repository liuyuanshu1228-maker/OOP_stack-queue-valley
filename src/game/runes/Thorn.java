package game.runes;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Ground planted by a completed {@link SquarePattern}. It blocks entry for a
 * limited number of turns, then reverts to the ground it replaced.
 * <p>
 * TODO: once REQ3 publishes its planned {@code TemporaryGround} base class
 * (SlimeGround/SaltGround/TrapGround will extend it too), this class should
 * extend it instead of keeping its own {@code originalGround}/countdown, to
 * avoid duplicating that "revert to original ground after N turns" logic.
 */
public class Thorn extends Ground {

    private static final char DISPLAY_CHAR = 'T';
    private static final String NAME = "Thorn";
    /** How many turns a Thorn lasts before reverting. */
    private static final int DURATION_TURNS = 5;

    private final Ground originalGround;
    private int remainingTurns;

    /**
     * @param originalGround the ground this Thorn replaces, kept so it can be restored
     */
    public Thorn(Ground originalGround) {
        super(DISPLAY_CHAR, NAME);
        this.originalGround = originalGround;
        this.remainingTurns = DURATION_TURNS;
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    public void tick(Location location) {
        remainingTurns--;
        if (remainingTurns <= 0) {
            location.setGround(originalGround);
        }
    }
}
