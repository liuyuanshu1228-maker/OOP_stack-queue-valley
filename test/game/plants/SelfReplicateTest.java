package game.plants;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.Melon;
import game.crops.MelonSprout;
import game.crops.Strawberry;
import game.crops.StrawberrySeedling;
import game.crops.StrawberrySprout;
import game.crops.Tomato;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests plants spreading new plants onto the tiles around them.
 */
class SelfReplicateTest extends PlantTestBase {

    @Test
    void sproutSpreadsASeedlingOntoAdjacentSoil() {
        GameMap map = TestFarm.map(",,,", ",,,", ",,,");
        Location centre = map.at(1, 1);
        centre.setGround(new StrawberrySprout());

        TestFarm.tick(centre, 5);

        assertEquals(1, TestFarm.countGround(map, StrawberrySeedling.class));
        assertTrue(takeOutput().contains("takes root at (0, 0)"));
    }

    @Test
    void spreadsOnlyOntoSoilNotDirt() {
        GameMap map = TestFarm.map("..,", "...", "...");
        Location centre = map.at(1, 1);
        centre.setGround(new MelonSprout());

        TestFarm.tick(centre, 10);

        assertInstanceOf(MelonSprout.class, map.at(2, 0).getGround(), "the only Soil tile");
        assertEquals(2, TestFarm.countGround(map, MelonSprout.class));
    }

    @Test
    void noAdjacentSoilMeansNothingHappens() {
        GameMap map = TestFarm.map("...", "...", "...");
        Location centre = map.at(1, 1);
        centre.setGround(new StrawberrySprout());

        TestFarm.tick(centre, 5);

        assertEquals(0, TestFarm.countGround(map, StrawberrySeedling.class));
        assertTrue(takeOutput().isEmpty());
    }

    @Test
    void growingTakesPriorityOverSpreadingOnTheSameTurn() {
        GameMap map = TestFarm.map(",,,", ",,,", ",,,");
        Location centre = map.at(1, 1);
        centre.setGround(new StrawberrySprout());
        TestFarm.tick(centre, 5);

        TestFarm.tick(centre, 5);

        assertInstanceOf(Strawberry.class, centre.getGround());
        assertEquals(1, TestFarm.countGround(map, StrawberrySeedling.class), "no second spread on turn 10");
    }

    @Test
    void failedGrowthFallsThroughToSpreading() {
        GameMap map = TestFarm.map(",,,", ",,,", ",,,");
        Location centre = map.at(1, 1);
        centre.setGround(new StrawberrySprout());
        random.setRollsSucceed(false);

        TestFarm.tick(centre, 10);

        assertInstanceOf(StrawberrySprout.class, centre.getGround());
        assertEquals(2, TestFarm.countGround(map, StrawberrySeedling.class), "spread on turns 5 and 10");
    }

    @Test
    void melonCropSpreadsASproutEveryTwentyTurns() {
        GameMap map = TestFarm.map(",,,", ",,,", ",,,");
        Location centre = map.at(1, 1);
        centre.setGround(new Melon());

        TestFarm.tick(centre, 19);
        assertEquals(0, TestFarm.countGround(map, MelonSprout.class));

        TestFarm.tick(centre, 1);
        assertEquals(1, TestFarm.countGround(map, MelonSprout.class));
    }

    @Test
    void tomatoSpreadDependsOnItsChance() {
        GameMap lucky = TestFarm.map(",,,", ",,,", ",,,");
        lucky.at(1, 1).setGround(new Tomato());
        TestFarm.tick(lucky.at(1, 1), 20);

        GameMap unlucky = TestFarm.map(",,,", ",,,", ",,,");
        unlucky.at(1, 1).setGround(new Tomato());
        random.setRollsSucceed(false);
        TestFarm.tick(unlucky.at(1, 1), 20);

        assertEquals(2, TestFarm.countGround(lucky, Tomato.class));
        assertEquals(1, TestFarm.countGround(unlucky, Tomato.class));
    }
}
