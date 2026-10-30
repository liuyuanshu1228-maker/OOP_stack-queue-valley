package game.random;

import java.util.List;
import java.util.Random;

/**
 * Random rolls shared across otherwise unrelated parts of the game (items,
 * trading, crops, ...).
 * <p>
 * Every caller that needs randomness is given a RandomUtils instance through
 * its own constructor, rather than reaching for a shared global one. A
 * subclass can change how randomness works (e.g. a fixed-seed version for
 * reproducible testing) by overriding {@link #doRandomInt(int)} and handing
 * that instance to whichever callers should use it, without changing this
 * class or any caller. Chances are rolled on top of {@link #doRandomInt(int)},
 * so that one override covers them too.
 */
public class RandomUtils {

    /** The scale that percentage chances are rolled against. */
    private static final int PERCENT_SCALE = 100;

    /** The source of randomness used by this instance. */
    private final Random random;

    /**
     * Creates an instance backed by an unseeded {@link Random}.
     */
    public RandomUtils() {
        this(new Random());
    }

    /**
     * Creates an instance backed by the given {@link Random}, e.g. a seeded
     * one for reproducible results.
     *
     * @param random the source of randomness to use
     */
    public RandomUtils(Random random) {
        this.random = random;
    }

    /**
     * Rolls a random chance.
     *
     * @param percent the chance of success, out of 100
     * @return {@code true} if the roll succeeds, {@code false} otherwise
     */
    public boolean rollPercent(int percent) {
        return doRollPercent(percent);
    }

    /**
     * Returns a uniformly random integer from 0 (inclusive) to bound (exclusive).
     *
     * @param bound the upper bound (exclusive); must be positive
     * @return a random integer in the range [0, bound)
     */
    public int randomInt(int bound) {
        return doRandomInt(bound);
    }

    /**
     * Picks one element of the given non-empty list uniformly at random.
     *
     * @param options the list to pick from; must not be empty
     * @param <T>     the type of the list elements
     * @return a randomly chosen element
     */
    public <T> T pickRandom(List<T> options) {
        return options.get(randomInt(options.size()));
    }

    /**
     * How this instance rolls a percentage chance. It is built on
     * {@link #doRandomInt(int)}, so overriding that method already changes the
     * result of this one; override this method itself only to change how a
     * chance is judged on its own. Used by {@link #rollPercent(int)}.
     *
     * @param percent the chance of success, out of 100
     * @return {@code true} if the roll succeeds, {@code false} otherwise
     */
    protected boolean doRollPercent(int percent) {
        return doRandomInt(PERCENT_SCALE) < percent;
    }

    /**
     * How this instance picks a bounded random integer. Subclasses override
     * this to change the behaviour of {@link #randomInt(int)} and
     * {@link #pickRandom(List)}.
     *
     * @param bound the upper bound (exclusive); must be positive
     * @return a random integer in the range [0, bound)
     */
    protected int doRandomInt(int bound) {
        return random.nextInt(bound);
    }
}
