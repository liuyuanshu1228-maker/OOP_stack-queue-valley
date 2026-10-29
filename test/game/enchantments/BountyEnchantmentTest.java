package game.enchantments;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.StrawberrySeedling;
import game.crops.StrawberrySprout;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the Bounty Enchantment forces an extra growth attempt on every
 * Plant occupying an affected tile. The meaningful case is a plant whose
 * natural growth roll just failed on its periodic turn: Bounty gives it one
 * more, immediate attempt instead of making it wait for the next periodic
 * turn. Also checks that every message it returns, including the "nothing to
 * grow" fallback, names coordinates.
 */
class BountyEnchantmentTest {

    /** How many turns of age a StrawberrySeedling waits between growth attempts. */
    private static final int GROWTH_INTERVAL_TURNS = 5;

    private final GameMap map = TestFarm.map("...", "...", "...");

    private FixedRandom random;

    @BeforeEach
    void setUp() {
        random = FixedRandom.install();
    }

    @AfterEach
    void tearDown() {
        FixedRandom.uninstall();
    }

    /**
     * @return the tile of a StrawberrySeedling that just reached its periodic
     * growth turn but failed the roll, so it is still a seedling
     */
    private Location seedlingThatJustFailedToGrow() {
        Location location = map.at(1, 1);
        location.setGround(new StrawberrySeedling());
        random.setRollsSucceed(false);
        TestFarm.tick(location, GROWTH_INTERVAL_TURNS);
        return location;
    }

    @Test
    void forcesGrowthEvenRightAfterTheNaturalRollJustFailed() {
        Location location = seedlingThatJustFailedToGrow();
        assertInstanceOf(StrawberrySeedling.class, location.getGround(),
                "test setup: the natural growth roll should have failed");
        random.setRollsSucceed(true);

        new BountyEnchantment().applyTo(List.of(location), null, map.at(0, 0));

        assertInstanceOf(StrawberrySprout.class, location.getGround());
    }

    @Test
    void successMessageNamesTheGrownTiles() {
        Location location = seedlingThatJustFailedToGrow();
        random.setRollsSucceed(true);

        String message = new BountyEnchantment().applyTo(List.of(location), null, map.at(0, 0));

        assertTrue(message.contains("(1, 1)"), message);
    }

    @Test
    void aTileWithNoPlantIsSkippedAndNamesTheOriginInstead() {
        Location bareTile = map.at(2, 2);
        Location origin = map.at(0, 0);

        String message = new BountyEnchantment().applyTo(List.of(bareTile), null, origin);

        assertTrue(message.contains("(0, 0)"), message);
    }

    @Test
    void emptyAffectedTilesGrowsNothingAndNamesTheOrigin() {
        Location origin = map.at(1, 1);

        String message = new BountyEnchantment().applyTo(List.of(), null, origin);

        assertTrue(message.contains("(1, 1)"), message);
    }
}
