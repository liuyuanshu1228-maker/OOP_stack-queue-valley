package game.runes;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * Ground raised by a completed {@link TrianglePattern}. The next rune
 * formation triggered on this tile consumes its one-time boost (see
 * {@link PlaceRuneStoneAction}); after that it behaves like plain ground.
 */
public class Altar extends Ground implements Boostable {

    private static final char DISPLAY_CHAR = 'A';
    private static final String NAME = "Altar";

    /** Whether this Altar's one-time boost is still available. */
    private boolean boostReady = true;

    public Altar() {
        super(DISPLAY_CHAR, NAME);
    }

    @Override
    public boolean consumeBoost() {
        if (!boostReady) {
            return false;
        }
        boostReady = false;
        return true;
    }
}
