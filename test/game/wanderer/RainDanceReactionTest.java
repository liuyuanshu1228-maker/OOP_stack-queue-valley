package game.wanderer;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.Strawberry;
import game.positions.area.AreaFactory;
import game.positions.farm.Soil;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import game.weather.WeatherReading;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the reaction to rain: tilling bare ground and watering crops over an
 * area that grows with the rainfall.
 */
class RainDanceReactionTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);
    private final GameMap map = TestFarm.map(".....", ".....", ".....", ".....", ".....");

    @Test
    void tillsOnlyTheAdjacentTilesAtLightRainfall() {
        Location here = map.at(2, 2);

        new RainDanceReaction(new WeatherReading("Rain", 12.0, 2.0, 0.5), areaFactory).reactAround(here);

        assertEquals(8, TestFarm.countGround(map, Soil.class), "radius 1: the 8 adjacent tiles only");
    }

    @Test
    void theTilledAreaGrowsWithHeavierRainfall() {
        Location here = map.at(2, 2);

        new RainDanceReaction(new WeatherReading("Rain", 12.0, 2.0, 2.1), areaFactory).reactAround(here);

        assertEquals(24, TestFarm.countGround(map, Soil.class), "radius 3 covers every tile but the centre");
    }

    @Test
    void watersEveryFreelyWaterableCropInRange() {
        Location here = map.at(2, 2);
        Strawberry crop = new Strawberry(random, areaFactory);
        map.at(2, 1).setGround(crop);

        new RainDanceReaction(new WeatherReading("Rain", 12.0, 2.0, 0.5), areaFactory).reactAround(here);

        assertEquals(1, crop.getWateringCount());
    }

    @Test
    void messageReportsTilledAndWateredCountsWithCoordinates() {
        Location here = map.at(2, 2);

        String report = new RainDanceReaction(new WeatherReading("Rain", 12.0, 2.0, 0.5), areaFactory)
                .reactAround(here);

        assertTrue(report.contains("(2, 2)"), report);
        assertTrue(report.contains("8 bare tiles turn to Soil"), report);
        assertTrue(report.contains(map.at(1, 1).toString()), "names every tilled tile: " + report);
        assertTrue(report.contains(map.at(3, 3).toString()), "names every tilled tile: " + report);
    }
}
