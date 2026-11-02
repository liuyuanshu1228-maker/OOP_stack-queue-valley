package game.enchantments;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.Strawberry;
import game.crops.StrawberrySeedling;
import game.crops.StrawberrySprout;
import game.plants.Plant;
import game.positions.area.AreaFactory;
import game.status.GrowthPausedStatus;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the Bounty Enchantment forces an extra growth attempt on every
 * Plant occupying an affected tile. Bounty skips the plant's own interval and
 * chance, so it works on a turn the plant is not due to grow (ages 2 and 4 of
 * a 5-turn interval), for growing and for dropping fruit, and right after a
 * natural roll failed. A paused plant
 * still does not grow. Also checks that every message it returns, including
 * the "nothing to grow" fallback, names coordinates.
 */
class BountyEnchantmentTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);

    /** How many turns of age a StrawberrySeedling waits between growth attempts. */
    private static final int GROWTH_INTERVAL_TURNS = 5;

    private final GameMap map = TestFarm.map("...", "...", "...");

    /**
     * @return the tile of a StrawberrySeedling that just reached its periodic
     * growth turn but failed the roll, so it is still a seedling
     */
    private Location seedlingThatJustFailedToGrow() {
        Location location = map.at(1, 1);
        location.setGround(new StrawberrySeedling(random, areaFactory));
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
    void forcesGrowthOnATurnTheSeedlingIsNotDueToGrow() {
        Location location = map.at(1, 1);
        location.setGround(new StrawberrySeedling(random, areaFactory));
        TestFarm.tick(location, 2);
        assertInstanceOf(StrawberrySeedling.class, location.getGround(),
                "test setup: at age 2 the seedling is not due to grow until age 5");

        new BountyEnchantment().applyTo(List.of(location), null, map.at(0, 0));

        assertInstanceOf(StrawberrySprout.class, location.getGround());
    }

    @Test
    void forcesGrowthOneTurnBeforeTheSeedlingIsDue() {
        Location location = map.at(1, 1);
        location.setGround(new StrawberrySeedling(random, areaFactory));
        TestFarm.tick(location, GROWTH_INTERVAL_TURNS - 1);
        assertInstanceOf(StrawberrySeedling.class, location.getGround(),
                "test setup: at age 4 the seedling is one turn short of its natural growth");

        new BountyEnchantment().applyTo(List.of(location), null, map.at(0, 0));

        assertInstanceOf(StrawberrySprout.class, location.getGround());
    }

    @Test
    void forcesAStrawberryToDropFruitOnATurnItIsNotDue() {
        Location location = map.at(1, 1);
        location.setGround(new Strawberry(random, areaFactory));
        TestFarm.tick(location, 2);
        assertEquals(0, TestFarm.countItems(map), "test setup: at age 2 the strawberry is not due to drop fruit");

        String message = new BountyEnchantment().applyTo(List.of(location), null, map.at(0, 0));

        assertEquals(1, TestFarm.countItems(map), "Bounty forced one strawberry to drop");
        assertTrue(message.contains("drops a"), message);
    }

    @Test
    void aPlantWhoseGrowthIsPausedDoesNotGrow() {
        Location location = seedlingThatJustFailedToGrow();
        location.getGroundAs(Plant.class).addStatus(new GrowthPausedStatus(3));
        random.setRollsSucceed(true);

        String message = new BountyEnchantment().applyTo(List.of(location), null, map.at(0, 0));

        assertInstanceOf(StrawberrySeedling.class, location.getGround(), "a frost pause also stops magical growth");
        assertTrue(message.contains("finds no plant ready to grow"), message);
    }

    @Test
    void successMessageNamesTheGrownTiles() {
        Location location = seedlingThatJustFailedToGrow();
        random.setRollsSucceed(true);

        String message = new BountyEnchantment().applyTo(List.of(location), null, map.at(0, 0));

        assertTrue(message.contains("(1, 1)"), message);
        assertTrue(message.contains("grows into a Strawberry Sprout"), "says what the plant grew into: " + message);
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
