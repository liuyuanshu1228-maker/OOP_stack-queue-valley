package game.wanderer;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.StackQueueValleyWorld;
import game.actors.Farmer;
import game.crops.StrawberrySeedling;
import game.positions.area.AreaFactory;
import game.status.GrowthPausedStatus;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import game.weather.MapGeography;
import game.weather.WeatherReading;
import game.weather.WeatherSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the Action an actor performs to ask a Wanderer for tomorrow's
 * forecast: it either reports why the forecast cannot be read, that nothing
 * unusual is coming, or brings the matching {@link ForecastOmen} upon the
 * asker and the farm, depending only on {@link WeatherSource} and
 * {@link WeatherInterpreter}.
 */
class AskForecastActionTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);
    private final FixedWeatherSource weatherSource = new FixedWeatherSource();
    private final MapGeography mapGeography = new MapGeography(0, 0, 1);
    private final WeatherInterpreter weatherInterpreter = new WeatherInterpreter(areaFactory, random);
    private final Wanderer wanderer =
            new Wanderer(random, weatherSource, mapGeography, weatherInterpreter);

    private GameMap map;
    private final Farmer asker = new Farmer("Test");
    private AskForecastAction action;

    @BeforeEach
    void setUp() throws Exception {
        map = TestFarm.map(".......", ".......", ".......", ".......", ".......", ".......", ".......");
        new StackQueueValleyWorld(new Display()).addGameMap(map);
        map.at(3, 3).addActor(wanderer);
        map.at(3, 4).addActor(asker);
        action = new AskForecastAction(wanderer, weatherSource, mapGeography, weatherInterpreter);
    }

    @Test
    void reportsAnUnreadableForecastWhenNoReadingIsAvailable() {
        String report = action.execute(asker, map);

        assertEquals("The Wanderer squints at the sky, but tomorrow's weather is unreadable.", report);
    }

    @Test
    void reportsNothingUnusualWhenNoOmenApplies() {
        weatherSource.setTomorrow(new WeatherReading("Clear", 15.0, 0, 0));

        String report = action.execute(asker, map);

        assertEquals("The Wanderer shrugs: tomorrow looks like an ordinary day.", report);
    }

    @Test
    void bringsTheMatchingOmenUponTheAskerAndTheFarm() {
        weatherSource.setTomorrow(new WeatherReading("Clear", -1.0, 0, 0));
        StrawberrySeedling nearbyPlant = new StrawberrySeedling(random, areaFactory);
        map.at(3, 2).setGround(nearbyPlant);

        String report = action.execute(asker, map);

        assertTrue(report.contains("(3, 3)"), report);
        assertTrue(nearbyPlant.hasStatus(GrowthPausedStatus.class), "the FrostOmen's own effect was carried out");
    }

    @Test
    void menuDescriptionNamesTheAskerAndTheWanderer() {
        String description = action.menuDescription(asker);

        assertEquals("Farmer Test (100/100) asks Wanderer (50/50) about tomorrow's weather", description);
    }
}
