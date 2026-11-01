package game.wanderer;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.actors.Farmer;
import game.positions.area.AreaFactory;
import game.positions.farm.Dirt;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import game.weather.WeatherReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the reaction to a strong wind: sheltering free adjacent tiles for a
 * duration that grows with the wind speed, then reverting them.
 */
class StormShelterReactionTest {

    private final AreaFactory areaFactory = new AreaFactory(new FixedRandom());
    private final GameMap map = TestFarm.map("...", "...", "...");

    @BeforeEach
    void setUp() {
        // containsAnActor (used by StormShelterReaction itself) and addActor
        // both need the map registered with a World first, exactly as
        // game.pests.PestTurnTest and game.wanderer.WandererTest do.
        new StackQueueValleyWorld(new Display()).addGameMap(map);
    }

    @Test
    void sheltersEveryFreeAdjacentTile() {
        Location here = map.at(1, 1);

        new StormShelterReaction(new WeatherReading("Clear", 20.0, 12.0, 0), areaFactory).reactAround(here);

        assertEquals(8, TestFarm.countGround(map, StormShelterGround.class));
    }

    @Test
    void leavesATileWithAnActorOnItAlone() throws Exception {
        Location occupied = map.at(0, 0);
        occupied.addActor(new Farmer("Test"));
        Location here = map.at(1, 1);

        new StormShelterReaction(new WeatherReading("Clear", 20.0, 12.0, 0), areaFactory).reactAround(here);

        assertEquals(7, TestFarm.countGround(map, StormShelterGround.class));
        assertEquals(Dirt.class, occupied.getGround().getClass());
    }

    @Test
    void durationGrowsWithWindSpeedAndRevertsOnceItExpires() {
        Location here = map.at(1, 1);
        Location shelteredTile = map.at(0, 0);

        new StormShelterReaction(new WeatherReading("Clear", 20.0, 2.0, 0), areaFactory).reactAround(here);
        TestFarm.tick(shelteredTile, 4);
        assertEquals(StormShelterGround.class, shelteredTile.getGround().getClass(), "lasts 3 + 2 = 5 turns");

        TestFarm.tick(shelteredTile, 1);
        assertEquals(Dirt.class, shelteredTile.getGround().getClass());
    }

    @Test
    void messageReportsCoordinatesWindSpeedAndTilesSheltered() {
        Location here = map.at(1, 1);

        String report = new StormShelterReaction(new WeatherReading("Clear", 20.0, 2.0, 0), areaFactory)
                .reactAround(here);

        assertTrue(report.contains("(1, 1)"), report);
        assertTrue(report.contains("2.0 m/s"), report);
        assertTrue(report.contains("8 tiles"), report);
        assertTrue(report.contains(map.at(0, 0).toString()), "names every sheltered tile: " + report);
        assertTrue(report.contains(map.at(2, 2).toString()), "names every sheltered tile: " + report);
    }
}
