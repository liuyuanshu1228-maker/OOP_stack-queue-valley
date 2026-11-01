package game.wanderer;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.area.AreaFactory;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import game.weather.WeatherReading;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the reaction to cold weather: how many adjacent tiles catch alight.
 */
class CampfireReactionTest {

    private final AreaFactory areaFactory = new AreaFactory(new FixedRandom());
    private final GameMap map = TestFarm.map("...", "...", "...");

    @Test
    void lightsOnlyOneTileAtTheColdThreshold() {
        Location here = map.at(1, 1);

        new CampfireReaction(new WeatherReading("Clear", 10.0, 0, 0), areaFactory, 10.0).reactAround(here);

        assertEquals(1, TestFarm.countItems(map));
    }

    @Test
    void moreTilesLightTheColderItGets() {
        Location here = map.at(1, 1);

        new CampfireReaction(new WeatherReading("Clear", 4.0, 0, 0), areaFactory, 10.0).reactAround(here);

        assertEquals(6, TestFarm.countItems(map));
    }

    @Test
    void neverLightsMoreThanTheEightAdjacentTiles() {
        Location here = map.at(1, 1);

        new CampfireReaction(new WeatherReading("Clear", -50.0, 0, 0), areaFactory, 10.0).reactAround(here);

        assertEquals(8, TestFarm.countItems(map));
    }

    @Test
    void messageNamesTheCoordinatesAndTheTemperature() {
        Location here = map.at(1, 1);

        String report = new CampfireReaction(new WeatherReading("Clear", 4.0, 0, 0), areaFactory, 10.0)
                .reactAround(here);

        assertTrue(report.contains("(1, 1)"), report);
        assertTrue(report.contains("4.0"), report);
    }
}
