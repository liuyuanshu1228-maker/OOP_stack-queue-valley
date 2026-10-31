package game.wanderer;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Farmer;
import game.statistics.StatisticAdjuster;
import game.testsupport.TestFarm;
import game.weather.WeatherReading;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the omen of a hot, dry forecast: drains the asking actor's stamina
 * for 3 turns, 2 points at a time, via {@link DroughtWearinessStatus}.
 * <p>
 * This omen affects only the asker, not an area, so unlike
 * {@link FrostOmenTest} and {@link SowingOmenTest} a small 1x1 map is enough.
 */
class DroughtOmenTest {

    private final GameMap map = TestFarm.map(".");
    private final Location here = map.at(0, 0);
    private final Farmer asker = new Farmer("Test");

    @Test
    void attachesAWearinessStatusToTheAsker() {
        new DroughtOmen(new WeatherReading("Clear", 30.0, 0, 0)).bringUpon(asker, here);

        assertTrue(asker.hasStatus(DroughtWearinessStatus.class));
    }

    @Test
    void drainsStaminaEachTurnForThreeTurnsThenExpires() {
        new DroughtOmen(new WeatherReading("Clear", 30.0, 0, 0)).bringUpon(asker, here);

        asker.tickStatuses(here);
        assertEquals(98, asker.getStatistic(ActorStatistics.STAMINA));
        asker.tickStatuses(here);
        assertEquals(96, asker.getStatistic(ActorStatistics.STAMINA));
        asker.tickStatuses(here);
        assertEquals(94, asker.getStatistic(ActorStatistics.STAMINA));

        assertFalse(asker.hasStatus(DroughtWearinessStatus.class), "expired after its 3rd turn");
    }

    @Test
    void neverDrainsStaminaBelowZero() {
        new StatisticAdjuster(asker).trySpend(ActorStatistics.STAMINA, 99);
        assertEquals(1, asker.getStatistic(ActorStatistics.STAMINA));

        new DroughtOmen(new WeatherReading("Clear", 30.0, 0, 0)).bringUpon(asker, here);
        asker.tickStatuses(here);

        assertEquals(1, asker.getStatistic(ActorStatistics.STAMINA), "too little stamina to drain, so left unchanged");
    }

    @Test
    void messageReportsCoordinatesTheAskerAndTemperature() {
        String report = new DroughtOmen(new WeatherReading("Clear", 30.0, 0, 0)).bringUpon(asker, here);

        assertTrue(report.contains("(0, 0)"), report);
        assertTrue(report.contains(asker.toString()), report);
        assertTrue(report.contains("30.0"), report);
    }
}
