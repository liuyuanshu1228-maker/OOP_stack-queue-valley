package game.wanderer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.actors.Farmer;
import game.positions.area.AreaFactory;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import game.weather.WeatherReading;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
        for (Location tile : areaFactory.adjacentTo(here).tiles()) {
            if (!tile.getItems().isEmpty()) {
                assertTrue(report.contains(tile.toString()), "names every tile set alight: " + report);
            }
        }
    }

    @Test
    void anActorInTheCampfireIsBurntAndTheBurnIsReportedWithItsTile() throws Exception {
        new StackQueueValleyWorld(new Display()).addGameMap(map);
        Location here = map.at(1, 1);
        new CampfireReaction(new WeatherReading("Clear", 10.0, 0, 0), areaFactory, 10.0).reactAround(here);
        Location fireTile = null;
        for (Location tile : areaFactory.adjacentTo(here).tiles()) {
            if (!tile.getItems().isEmpty()) {
                fireTile = tile;
            }
        }
        Farmer victim = new Farmer("Victim");
        fireTile.addActor(victim);
        int healthBefore = victim.getStatistic(ActorStatistics.HEALTH);

        String printed = burnOneTurn(fireTile, victim);

        assertEquals(healthBefore - 5, victim.getStatistic(ActorStatistics.HEALTH));
        assertTrue(printed.contains("burns for 5 damage at " + fireTile), printed);
    }

    /**
     * Runs one turn of the fire on a tile and of the statuses on the actor
     * standing there, and returns what was printed.
     *
     * @param tile the burning tile
     * @param victim the actor standing on it
     * @return everything printed during the turn
     */
    private String burnOneTurn(Location tile, Actor victim) {
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(printed, true));
        try {
            tile.tick();
            victim.tickStatuses(tile);
        } finally {
            System.setOut(original);
        }
        return printed.toString();
    }
}
