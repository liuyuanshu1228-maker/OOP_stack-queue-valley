package game.wanderer;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Farmer;
import game.crops.StrawberrySeedling;
import game.positions.area.AreaFactory;
import game.status.GrowthPausedStatus;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import game.weather.WeatherReading;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the omen of a frosty forecast: pauses the growth of every plant
 * within radius 2 of the Wanderer, for longer the colder it will be.
 * <p>
 * Every map is 7x7 so that a plant placed at distance 3 from the centre
 * (e.g. the top edge) is genuinely outside the radius-2 area; a 5x5 map
 * would leave no tile outside that range to test against.
 */
class FrostOmenTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);
    private final GameMap map = TestFarm.map(
            ".......", ".......", ".......", ".......", ".......", ".......", ".......");
    private final Farmer asker = new Farmer("Test");

    @Test
    void pausesAPlantWithinTheRadius() {
        Location here = map.at(3, 3);
        StrawberrySeedling plant = new StrawberrySeedling(random, areaFactory);
        map.at(3, 2).setGround(plant);

        new FrostOmen(new WeatherReading("Clear", -1.0, 0, 0), areaFactory, 2.0).bringUpon(asker, here);

        assertTrue(plant.hasStatus(GrowthPausedStatus.class));
    }

    @Test
    void leavesAPlantOutsideTheRadiusUntouched() {
        Location here = map.at(3, 3);
        StrawberrySeedling farPlant = new StrawberrySeedling(random, areaFactory);
        map.at(3, 0).setGround(farPlant);

        new FrostOmen(new WeatherReading("Clear", -1.0, 0, 0), areaFactory, 2.0).bringUpon(asker, here);

        assertFalse(farPlant.hasStatus(GrowthPausedStatus.class));
    }

    @Test
    void thePauseLastsLongerTheColderTomorrowWillBeThenExpires() {
        Location here = map.at(3, 3);
        StrawberrySeedling plant = new StrawberrySeedling(random, areaFactory);
        Location plantTile = map.at(3, 2);
        plantTile.setGround(plant);

        new FrostOmen(new WeatherReading("Clear", -1.0, 0, 0), areaFactory, 2.0).bringUpon(asker, here);
        // base 2 turns + max(0, 2.0 - (-1.0)) = 2 + 3 = 5 turns
        TestFarm.tick(plantTile, 4);
        assertTrue(plant.hasStatus(GrowthPausedStatus.class), "still paused after 4 of 5 turns");

        TestFarm.tick(plantTile, 1);
        assertFalse(plant.hasStatus(GrowthPausedStatus.class), "expired after the 5th turn");
    }

    @Test
    void messageReportsCoordinatesTemperatureAndPausedPlantCount() {
        Location here = map.at(3, 3);
        map.at(3, 2).setGround(new StrawberrySeedling(random, areaFactory));
        map.at(2, 3).setGround(new StrawberrySeedling(random, areaFactory));

        String report = new FrostOmen(new WeatherReading("Clear", -1.0, 0, 0), areaFactory, 2.0)
                .bringUpon(asker, here);

        assertTrue(report.contains("(3, 3)"), report);
        assertTrue(report.contains("-1.0"), report);
        assertTrue(report.contains("2 plants"), report);
    }
}
