package game.testsupport;

import game.random.RandomUtils;

/**
 * A {@link RandomUtils} with no randomness, so tests are deterministic.
 * <p>
 * Every random integer is 0, so random picks always take the first option.
 * A chance of 100% always succeeds and a chance of 0% always fails; any
 * other chance succeeds or fails depending on {@link #setRollsSucceed}.
 * <p>
 * Unlike production code, a test simply constructs a {@code FixedRandom} and
 * passes it to whatever it is testing, through that class's own constructor:
 * there is no global instance to install, since {@link RandomUtils} has none.
 */
public class FixedRandom extends RandomUtils {

    /** The chance that always succeeds. */
    private static final int ALWAYS_PERCENT = 100;

    /** Whether a chance between 0% and 100% succeeds. */
    private boolean rollsSucceed = true;

    /**
     * Sets whether a chance between 0% and 100% succeeds from now on.
     *
     * @param rollsSucceed true to make those chances succeed
     */
    public void setRollsSucceed(boolean rollsSucceed) {
        this.rollsSucceed = rollsSucceed;
    }

    @Override
    protected boolean doRollPercent(int percent) {
        if (percent >= ALWAYS_PERCENT) {
            return true;
        }
        return percent > 0 && rollsSucceed;
    }

    @Override
    protected int doRandomInt(int bound) {
        return 0;
    }
}
