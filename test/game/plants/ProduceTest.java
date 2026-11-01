package game.plants;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.Cauliflower;
import game.crops.Parsnip;
import game.crops.Strawberry;
import game.crops.Tomato;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests crops dropping produce onto the tiles around them. Dirt is used
 * around the crops so that spreading cannot happen.
 */
class ProduceTest extends PlantTestBase {

    @Test
    void aStrawberryDropsAStrawberryEveryFiveTurns() {
        assertDropsProduceEvery(new Strawberry(random, areaFactory), 5, "Strawberry");
    }

    @Test
    void aTomatoDropsATomatoEverySevenTurns() {
        assertDropsProduceEvery(new Tomato(random, areaFactory), 7, "Tomato");
    }

    @Test
    void aParsnipDropsAParsnipEveryFifteenTurns() {
        assertDropsProduceEvery(new Parsnip(random, areaFactory), 15, "Parsnip");
    }

    private void assertDropsProduceEvery(Plant crop, int interval, String name) {
        GameMap map = TestFarm.map("...", "...", "...");
        Location centre = map.at(1, 1);
        centre.setGround(crop);

        TestFarm.tick(centre, interval - 1);
        assertEquals(0, TestFarm.countItems(map), "nothing before the interval");

        TestFarm.tick(centre, 1);
        assertEquals(1, TestFarm.countItems(map));
        assertTrue(centre.getItems().isEmpty(), "dropped on an adjacent tile, not its own");
        assertTrue(takeOutput().contains(name + " at (1, 1)"));

        TestFarm.tick(centre, interval);
        assertEquals(2, TestFarm.countItems(map), "drops again after another interval");
    }

    @Test
    void cauliflowerDoesNothingOnItsOwn() {
        GameMap map = TestFarm.map(",,,", ",,,", ",,,");
        map.at(1, 1).setGround(new Cauliflower(random, areaFactory));

        TestFarm.tick(map.at(1, 1), 60);

        assertEquals(0, TestFarm.countItems(map));
        assertEquals(1, TestFarm.countGround(map, Cauliflower.class));
        assertTrue(takeOutput().isEmpty());
    }
}
