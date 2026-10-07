package game;

import java.util.List;
import java.util.Random;

/**
 * Random rolls shared across otherwise unrelated parts of the game (items,
 * trading, crops, ...).
 * <p>
 * Callers use the static methods. Those delegate to a replaceable instance,
 * so a subclass can change how randomness works for the whole game (e.g. a
 * fixed-seed version for reproducible testing, or an always-succeed game
 * mode) by overriding {@link #doRollPercent(int)} and {@link #doRandomInt(int)}
 * and installing itself with {@link #setInstance(RandomUtils)}, without
 * changing this class or any caller.
 */
public class RandomUtils {

    /** The instance that every static method delegates to. */
    private static RandomUtils instance = new RandomUtils();

    /** The source of randomness used by this instance. */
    private final Random random;

    /**
     * Creates an instance backed by an unseeded {@link Random}.
     */
    protected RandomUtils() {
        this(new Random());
    }

    /**
     * Creates an instance backed by the given {@link Random}, e.g. a seeded
     * one for reproducible results.
     *
     * @param random the source of randomness to use
     */
    protected RandomUtils(Random random) {
        this.random = random;
    }

    /**
     * Replaces the randomness used by the whole game.
     *
     * @param newInstance the instance every static method will delegate to from now on
     */
    public static void setInstance(RandomUtils newInstance) {
        instance = newInstance;
    }

    /**
     * Rolls a random chance.
     *
     * @param percent the chance of success, out of 100
     * @return {@code true} if the roll succeeds, {@code false} otherwise
     */
    public static boolean rollPercent(int percent) {
        return instance.doRollPercent(percent);
    }

    /**
     * Returns a uniformly random integer from 0 (inclusive) to bound (exclusive).
     *
     * @param bound the upper bound (exclusive); must be positive
     * @return a random integer in the range [0, bound)
     */
    public static int randomInt(int bound) {
        return instance.doRandomInt(bound);
    }

    /**
     * Picks one element of the given non-empty list uniformly at random.
     *
     * @param options the list to pick from; must not be empty
     * @param <T>     the type of the list elements
     * @return a randomly chosen element
     */
    public static <T> T pickRandom(List<T> options) {
        return options.get(randomInt(options.size()));
    }

    /**
     * How this instance rolls a percentage chance. Subclasses override this
     * to change the behaviour of {@link #rollPercent(int)}.
     *
     * @param percent the chance of success, out of 100
     * @return {@code true} if the roll succeeds, {@code false} otherwise
     */
    protected boolean doRollPercent(int percent) {
        return random.nextInt(100) < percent;
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
