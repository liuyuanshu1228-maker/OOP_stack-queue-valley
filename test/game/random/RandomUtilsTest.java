package game.random;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests the game's one source of randomness. Every other test replaces it
 * with {@code FixedRandom}, so these tests are the only check on the real
 * rules. They stay repeatable by giving {@link RandomUtils} either a seeded
 * {@link Random} or a {@link FixedDraw} that always returns the same number.
 */
class RandomUtilsTest {

    /** The seed used wherever a test only needs many repeatable draws. */
    private static final long SEED = 42;

    /** How many draws a test makes when it checks a rule on every draw. */
    private static final int DRAWS = 1000;

    @Test
    void randomIntStaysWithinTheBound() {
        RandomUtils randomUtils = new RandomUtils(new Random(SEED));

        for (int i = 0; i < DRAWS; i++) {
            int value = randomUtils.randomInt(10);
            assertTrue(value >= 0 && value < 10, "drew " + value);
        }
    }

    @Test
    void theSameSeedGivesTheSameDraws() {
        RandomUtils first = new RandomUtils(new Random(SEED));
        RandomUtils second = new RandomUtils(new Random(SEED));

        for (int i = 0; i < 20; i++) {
            assertEquals(first.randomInt(100), second.randomInt(100), "draw " + i);
        }
    }

    @Test
    void aBoundOfOneAlwaysGivesZero() {
        RandomUtils randomUtils = new RandomUtils(new Random(SEED));

        for (int i = 0; i < 20; i++) {
            assertEquals(0, randomUtils.randomInt(1));
        }
    }

    @Test
    void aZeroPercentChanceNeverSucceeds() {
        RandomUtils randomUtils = new RandomUtils(new Random(SEED));

        for (int i = 0; i < DRAWS; i++) {
            assertFalse(randomUtils.rollPercent(0));
        }
    }

    @Test
    void aHundredPercentChanceAlwaysSucceeds() {
        RandomUtils randomUtils = new RandomUtils(new Random(SEED));

        for (int i = 0; i < DRAWS; i++) {
            assertTrue(randomUtils.rollPercent(100));
        }
    }

    @Test
    void aThirtyPercentChanceSucceedsOnDrawsZeroToTwentyNine() {
        // A draw of 0 to 99 succeeds when it is below the chance, so exactly
        // 30 of the 100 possible draws succeed. Checking 29 and 30 catches a
        // "<=" written in place of "<", which would add one percent.
        assertTrue(new RandomUtils(new FixedDraw(0)).rollPercent(30));
        assertTrue(new RandomUtils(new FixedDraw(29)).rollPercent(30));
        assertFalse(new RandomUtils(new FixedDraw(30)).rollPercent(30));
        assertFalse(new RandomUtils(new FixedDraw(99)).rollPercent(30));
    }

    @Test
    void pickRandomTakesTheElementAtTheDrawnIndex() {
        RandomUtils randomUtils = new RandomUtils(new FixedDraw(2));

        assertEquals("c", randomUtils.pickRandom(List.of("a", "b", "c", "d")));
    }

    @Test
    void pickRandomFromOneElementAlwaysReturnsIt() {
        RandomUtils randomUtils = new RandomUtils(new Random(SEED));

        for (int i = 0; i < 20; i++) {
            assertEquals("only", randomUtils.pickRandom(List.of("only")));
        }
    }

    @Test
    void aBoundOfZeroOrLessIsRejected() {
        RandomUtils randomUtils = new RandomUtils(new Random(SEED));

        assertRandomIntRejected(randomUtils, 0);
        assertRandomIntRejected(randomUtils, -5);
    }

    @Test
    void pickingFromAnEmptyListIsRejected() {
        RandomUtils randomUtils = new RandomUtils(new Random(SEED));

        try {
            randomUtils.pickRandom(List.of());
            fail("Expected picking from an empty list to be rejected");
        } catch (IllegalArgumentException expected) {
            // There is nothing to pick, so the pick was rejected.
        }
    }

    @Test
    void rollPercentAsksTheOverridableStep() {
        AlwaysLucky lucky = new AlwaysLucky();

        assertTrue(lucky.rollPercent(0), "the subclass decides, not the default rule");
        assertEquals(1, lucky.rollsAsked);
    }

    private void assertRandomIntRejected(RandomUtils randomUtils, int bound) {
        try {
            randomUtils.randomInt(bound);
            fail("Expected a bound of " + bound + " to be rejected");
        } catch (IllegalArgumentException expected) {
            // The bound was rejected, as it should be.
        }
    }

    /** A {@link Random} that always draws the same number, as long as it is below the bound. */
    private static class FixedDraw extends Random {

        private final int value;

        FixedDraw(int value) {
            this.value = value;
        }

        @Override
        public int nextInt(int bound) {
            if (bound <= 0) {
                throw new IllegalArgumentException("bound must be positive");
            }
            return value;
        }
    }

    /** A {@link RandomUtils} whose every chance succeeds, the way a "lucky day" variant would. */
    private static class AlwaysLucky extends RandomUtils {

        private int rollsAsked = 0;

        @Override
        protected boolean doRollPercent(int percent) {
            rollsAsked++;
            return true;
        }
    }
}
