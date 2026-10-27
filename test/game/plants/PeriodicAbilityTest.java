package game.plants;

import edu.monash.fit2099.engine.positions.Location;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the "every N turns, with a chance" rule that all plant abilities share.
 */
class PeriodicAbilityTest extends PlantTestBase {

    /** An ability that only counts how many times it was performed. */
    private static class CountingAbility extends PeriodicAbility {
        private int performed = 0;

        CountingAbility(int intervalTurns, int chancePercent) {
            super(intervalTurns, chancePercent);
        }

        @Override
        protected String perform(Plant plant, Location location) {
            performed++;
            return "performed";
        }
    }

    /** A plant with only the given ability. */
    private static class TestPlant extends Plant {
        TestPlant(PlantAbility ability) {
            super('p', "Test Plant", ability);
        }
    }

    private Location plantAt(PlantAbility ability) {
        Location location = TestFarm.map("...").at(1, 0);
        location.setGround(new TestPlant(ability));
        return location;
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 20})
    void performsOnlyOnMultiplesOfTheInterval(int interval) {
        CountingAbility ability = new CountingAbility(interval, 100);
        Location location = plantAt(ability);

        TestFarm.tick(location, interval - 1);
        assertEquals(0, ability.performed, "nothing before the first interval");

        TestFarm.tick(location, 1);
        assertEquals(1, ability.performed, "performs on turn N");

        TestFarm.tick(location, interval);
        assertEquals(2, ability.performed, "performs again on turn 2N");
    }

    @Test
    void failedChanceRollMeansNothingHappens() {
        CountingAbility ability = new CountingAbility(5, 50);
        Location location = plantAt(ability);
        random.setRollsSucceed(false);

        TestFarm.tick(location, 20);

        assertEquals(0, ability.performed);
        assertTrue(takeOutput().isEmpty(), "a turn where nothing happens is not reported");
    }

    @Test
    void zeroChanceNeverHappensEvenWhenRollsSucceed() {
        CountingAbility ability = new CountingAbility(1, 0);
        Location location = plantAt(ability);

        TestFarm.tick(location, 10);

        assertEquals(0, ability.performed);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -20})
    void intervalThatIsNotPositiveIsRejected(int interval) {
        assertThrows(IllegalArgumentException.class, () -> new CountingAbility(interval, 50));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101, 1000})
    void chanceOutsideZeroToHundredIsRejected(int chance) {
        assertThrows(IllegalArgumentException.class, () -> new CountingAbility(5, chance));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100})
    void chanceAtEitherLimitIsAccepted(int chance) {
        assertDoesNotThrow(() -> new CountingAbility(5, chance));
    }

    @Test
    void successfulAbilityIsReported() {
        Location location = plantAt(new CountingAbility(3, 100));

        TestFarm.tick(location, 3);

        assertEquals("performed", takeOutput().trim());
    }
}
