package game.pests;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.Strawberry;
import game.crops.StrawberrySeedling;
import game.crops.StrawberrySprout;
import game.positions.area.PlantableTile;
import game.positions.farm.Dirt;
import game.positions.farm.Soil;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests what a SlimeSnail does when it feeds.
 */
class SlimeSnailTest {

    private GameMap map;
    private Location centre;
    private final SlimeSnail snail = new SlimeSnail();

    @BeforeEach
    void setUp() {
        map = TestFarm.map(",.,", ".,.", ",.,");
        centre = map.at(1, 1);
    }

    @Test
    void setsThePlantBackOneStageAndSlimesTheBareGroundAround() {
        centre.setGround(new Strawberry());

        String report = snail.feedOn(centre);

        assertInstanceOf(StrawberrySprout.class, centre.getGround());
        assertEquals(8, TestFarm.countGround(map, SlimeGround.class));
        assertTrue(report.contains("Slime Snail"), report);
    }

    @Test
    void aPlantWithNoEarlierStageDiesBackToSoil() {
        centre.setGround(new StrawberrySeedling());

        String report = snail.feedOn(centre);

        assertInstanceOf(Soil.class, centre.getGround());
        assertFalse(report.isEmpty());
    }

    @Test
    void doesNothingWhereThereIsNoPlant() {
        Ground before = centre.getGround();

        String report = snail.feedOn(centre);

        assertEquals("", report);
        assertSame(before, centre.getGround());
        assertEquals(0, TestFarm.countGround(map, SlimeGround.class));
    }

    @Test
    void doesNotSlimeANeighbouringPlant() {
        centre.setGround(new Strawberry());
        map.at(0, 0).setGround(new StrawberrySeedling());

        snail.feedOn(centre);

        assertInstanceOf(StrawberrySeedling.class, map.at(0, 0).getGround());
        assertEquals(7, TestFarm.countGround(map, SlimeGround.class));
    }

    @Test
    void slimedSoilCannotBePlantedUntilTheSlimeDries() {
        centre.setGround(new Strawberry());
        snail.feedOn(centre);
        Location slimedSoil = map.at(0, 0);
        PlantableTile plantable = new PlantableTile();

        Location slimedDirt = map.at(1, 0);
        assertFalse(plantable.isMetBy(slimedSoil));
        TestFarm.tick(slimedSoil, 5);
        TestFarm.tick(slimedDirt, 5);

        assertInstanceOf(Soil.class, slimedSoil.getGround());
        assertTrue(plantable.isMetBy(slimedSoil));
        assertInstanceOf(Dirt.class, slimedDirt.getGround(), "slimed Dirt dries back to Dirt");
    }
}
