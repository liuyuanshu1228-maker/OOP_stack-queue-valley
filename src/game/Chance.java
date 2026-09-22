package game;

import java.util.Random;

/**
 * Utility for rolling percentage-based random outcomes.
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
}
