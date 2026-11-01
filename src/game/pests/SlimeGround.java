package game.pests;

import edu.monash.fit2099.engine.positions.Ground;
import game.positions.Plantable;
import game.positions.TemporaryGround;

/**
 * Slime left by a {@link SlimeSnail}. It covers bare farm ground
 * for a few turns and then dries, putting the original ground back.
 * <p>
 * It is not {@link Plantable}, so nothing can be planted on a slimed tile
 * until the slime dries.
 * <p>
 * It lives in the pests package, next to the snail that makes it, so the
 * positions package does not depend on pests.
 */
class SlimeGround extends TemporaryGround {

    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = ':';

    /** The name of this ground. */
    private static final String NAME = "Slime";

    /** How many turns the slime lasts. */
    private static final int DURATION_TURNS = 5;

    /**
     * Creates slime over the given ground.
     *
     * @param originalGround the ground the slime covers, put back when it dries
     */
    SlimeGround(Ground originalGround) {
        super(DISPLAY_CHAR, NAME, originalGround, DURATION_TURNS);
    }
}
