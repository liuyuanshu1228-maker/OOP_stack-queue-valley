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
public class SlimeGround extends TemporaryGround {

    /**
     * Creates slime over the given ground, shown as ':'. It dries after 5 turns.
     *
     * @param originalGround the ground the slime covers, put back when it dries
     */
    public SlimeGround(Ground originalGround) {
        super(':', "Slime", originalGround, 5);
    }
}
