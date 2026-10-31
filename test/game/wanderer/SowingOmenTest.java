package game.wanderer;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Farmer;
import game.crops.StrawberrySeedling;
import game.positions.area.AreaFactory;
import game.positions.farm.Dirt;
import game.positions.farm.Soil;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import game.weather.WeatherReading;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the omen of a rainy forecast: a new Strawberry Seedling takes root
 * on every bare Soil tile within radius 2 of the Wanderer.
 * <p>
 * Every map is 7x7, for the same reason as {@link FrostOmenTest}: a tile at
 * distance 3 from the centre is genuinely outside the radius-2 area.
 */
class SowingOmenTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);
    private final Farmer asker = new Farmer("Test");

    @Test
    void sowsOnEveryBareSoilTileWithinTheRadius() {
        GameMap map = TestFarm.map(
                ",,,,,,,", ",,,,,,,", ",,,,,,,", ",,,,,,,", ",,,,,,,", ",,,,,,,", ",,,,,,,");
        Location here = map.at(3, 3);

        new SowingOmen(new WeatherReading("Rain", 15.0, 2.0, 1.0), areaFactory, random)
                .bringUpon(asker, here);

        assertEquals(24, TestFarm.countGround(map, StrawberrySeedling.class),
                "every tile of the 5x5 block around the centre, except the centre itself");
    }

    @Test
    void leavesTheFarTileOutsideTheRadiusAsPlainSoil() {
        GameMap map = TestFarm.map(
                ",,,,,,,", ",,,,,,,", ",,,,,,,", ",,,,,,,", ",,,,,,,", ",,,,,,,", ",,,,,,,");
        Location here = map.at(3, 3);
        Location farTile = map.at(3, 0);

        new SowingOmen(new WeatherReading("Rain", 15.0, 2.0, 1.0), areaFactory, random)
                .bringUpon(asker, here);

        assertEquals(Soil.class, farTile.getGround().getClass());
    }

    @Test
    void leavesBareDirtAloneBecauseItIsNotPlantable() {
        GameMap map = TestFarm.map(
                ".......", ".......", "...,...", ".......", ".......", ".......", ".......");
        Location here = map.at(3, 3);
        Location soilTile = map.at(3, 2);
        Location dirtTile = map.at(2, 3);

        new SowingOmen(new WeatherReading("Rain", 15.0, 2.0, 1.0), areaFactory, random)
                .bringUpon(asker, here);

        assertEquals(StrawberrySeedling.class, soilTile.getGround().getClass());
        assertEquals(Dirt.class, dirtTile.getGround().getClass());
    }

    @Test
    void messageReportsCoordinatesConditionAndSeedlingCount() {
        GameMap map = TestFarm.map(
                ".......", ".......", "...,...", ".......", ".......", ".......", ".......");
        Location here = map.at(3, 3);

        String report = new SowingOmen(new WeatherReading("Rain", 15.0, 2.0, 1.0), areaFactory, random)
                .bringUpon(asker, here);

        assertTrue(report.contains("(3, 3)"), report);
        assertTrue(report.contains("Rain"), report);
        assertTrue(report.contains("1 Strawberry Seedlings"), report);
    }
}
