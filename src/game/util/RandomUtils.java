package game.util;

import java.util.Random;

/**
 * Utility methods for small, commonly-needed random rolls shared across
 * otherwise unrelated parts of the game (items, trading, crops, ...).
 */
public final class RandomUtils {

    private static final Random RANDOM = new Random();

    private RandomUtils() {
        // Prevent instantiation; this class only provides static utility methods.
    }

    /**
     * Rolls a random chance.
     *
     * @param percent the chance of success, out of 100
     * @return {@code true} if the roll succeeds, {@code false} otherwise
     */
    public static boolean rollPercent(int percent) {
        return RANDOM.nextInt(100) < percent;
    }
}
