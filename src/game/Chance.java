package game;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Utility for rolling percentage-based random outcomes and for making random
 * picks.
 */
public final class Chance {

    private static final Random RANDOM = new Random();

    private Chance() {
    }

    /**
     * Rolls a chance out of 100. Values of {@code percentChance} at or below
     * 0 always fail, and values at or above 100 always succeed.
     *
     * @param percentChance the chance of success, out of 100
     * @return true with probability {@code percentChance}%
     */
    public static boolean rollPercent(int percentChance) {
        return RANDOM.nextInt(100) < percentChance;
    }

    /**
     * Picks one of the given options uniformly at random.
     *
     * @param options the options to pick from
     * @param <T> the type of the options
     * @return one randomly chosen option, or empty if there are no options
     */
    public static <T> Optional<T> pickOne(List<T> options) {
        if (options.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(options.get(RANDOM.nextInt(options.size())));
    }
}