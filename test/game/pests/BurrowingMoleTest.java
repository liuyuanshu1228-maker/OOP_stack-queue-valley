package game.pests;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.Strawberry;
import game.crops.StrawberrySprout;
import game.positions.area.AreaFactory;
import game.positions.farm.Dirt;
import game.positions.farm.Soil;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests what a BurrowingMole does when it feeds.
 */
class BurrowingMoleTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);

    private GameMap map;
    private Location centre;
    private final BurrowingMole mole = new BurrowingMole(random, areaFactory);

    @BeforeEach
    void setUp() {
        map = TestFarm.map(",,,", ",,,", ",,,");
        centre = map.at(1, 1);
    }

    @Test
    void setsThePlantBackAndCavesInTheFourStraightNeighbours() {
        centre.setGround(new Strawberry(random, areaFactory));

        String report = mole.feedOn(centre);

        assertInstanceOf(StrawberrySprout.class, centre.getGround());
        assertInstanceOf(Dirt.class, map.at(1, 0).getGround());
        assertInstanceOf(Dirt.class, map.at(0, 1).getGround());
        assertInstanceOf(Dirt.class, map.at(2, 1).getGround());
        assertInstanceOf(Dirt.class, map.at(1, 2).getGround());
        assertEquals(4, TestFarm.countGround(map, Soil.class), "the corners stay Soil");
        assertTrue(report.contains("Burrowing Mole"), report);
    }

    @Test
    void doesNothingWhereThereIsNoPlant() {
        String report = mole.feedOn(centre);

        assertEquals("", report);
        assertEquals(9, TestFarm.countGround(map, Soil.class));
        assertEquals(0, TestFarm.countItems(map));
    }

    @Test
    void doesNotCaveInANeighbouringPlant() {
        centre.setGround(new Strawberry(random, areaFactory));
        map.at(1, 0).setGround(new Strawberry(random, areaFactory));

        mole.feedOn(centre);

        assertInstanceOf(Strawberry.class, map.at(1, 0).getGround());
        assertEquals(3, TestFarm.countGround(map, Dirt.class));
    }

    @Test
    void saysNothingAboutCavingInWhenThereIsNoSoilAround() {
        map = TestFarm.map("...", "...", "...");
        centre = map.at(1, 1);
        centre.setGround(new Strawberry(random, areaFactory));

        String report = mole.feedOn(centre);

        assertInstanceOf(StrawberrySprout.class, centre.getGround());
        assertFalse(report.contains("caves in"), report);
    }
}
