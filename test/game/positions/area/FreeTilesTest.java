package game.positions.area;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.actors.Farmer;
import game.items.Milk;
import game.positions.farm.Dirt;
import game.positions.farm.Soil;
import game.random.RandomUtils;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests the free tiles of one map: something placed at random never lands on
 * a reserved or already used tile, a tile from another map is rejected, and a
 * full map fails at once instead of searching forever.
 * <p>
 * FreeTiles keeps drawing until it finds a free tile, so these tests use a
 * seeded {@link Random} rather than {@code FixedRandom}, whose draw never
 * changes and would never find another tile. The seed keeps every run the same.
 */
class FreeTilesTest {

    /** The seed that makes every random draw in these tests repeatable. */
    private static final long SEED = 42;

    /** How many fresh placements a test makes when it checks a rule many times. */
    private static final int TRIES = 100;

    private final RandomUtils randomUtils = new RandomUtils(new Random(SEED));
    private final AreaFactory areaFactory = new AreaFactory(randomUtils);

    @Test
    void aGroundGoesOnTheOnlyFreeTile() {
        GameMap map = TestFarm.map("...");
        FreeTiles freeTiles = new FreeTiles(map, randomUtils, areaFactory);
        freeTiles.reserve(map.at(0, 0));
        freeTiles.reserve(map.at(1, 0));

        freeTiles.placeGround(new Soil());

        assertEquals(Soil.class, map.at(2, 0).getGround().getClass());
    }

    @Test
    void aReservedTileIsNeverUsed() {
        GameMap map = TestFarm.map("..");
        for (int i = 0; i < TRIES; i++) {
            map.at(0, 0).setGround(new Dirt());
            map.at(1, 0).setGround(new Dirt());
            FreeTiles freeTiles = new FreeTiles(map, randomUtils, areaFactory);
            freeTiles.reserve(map.at(0, 0));

            freeTiles.placeGround(new Soil());

            assertEquals(Dirt.class, map.at(0, 0).getGround().getClass(), "try " + i);
        }
    }

    @Test
    void aTileIsNeverUsedTwice() {
        GameMap map = TestFarm.map("...", "...", "...");
        FreeTiles freeTiles = new FreeTiles(map, randomUtils, areaFactory);

        for (int i = 0; i < 9; i++) {
            freeTiles.placeItem(new Milk(randomUtils));
        }

        for (Location tile : areaFactory.wholeMap(map).tiles()) {
            assertEquals(1, tile.getItems().size(), "one item on " + tile);
        }
    }

    @Test
    void reserveAroundTakesTheTileAndAllItsNeighbours() {
        GameMap map = TestFarm.map("...", "...", "...");
        FreeTiles freeTiles = new FreeTiles(map, randomUtils, areaFactory);

        freeTiles.reserveAround(map.at(1, 1));

        assertFull(freeTiles);
    }

    @Test
    void reserveAroundInACornerTakesOnlyTheTilesOnTheMap() {
        GameMap map = TestFarm.map("...", "...", "...");
        FreeTiles freeTiles = new FreeTiles(map, randomUtils, areaFactory);
        freeTiles.reserveAround(map.at(0, 0));

        for (int i = 0; i < 5; i++) {
            freeTiles.placeGround(new Soil());
        }

        assertEquals(Dirt.class, map.at(0, 0).getGround().getClass());
        assertEquals(Dirt.class, map.at(1, 0).getGround().getClass());
        assertEquals(Dirt.class, map.at(0, 1).getGround().getClass());
        assertEquals(Dirt.class, map.at(1, 1).getGround().getClass());
        assertEquals(5, TestFarm.countGround(map, Soil.class), "the other five tiles were free");
    }

    @Test
    void aFullMapFailsAtOnceInsteadOfSearchingForever() {
        GameMap map = TestFarm.map("..");
        FreeTiles freeTiles = new FreeTiles(map, randomUtils, areaFactory);
        freeTiles.placeGround(new Soil());
        freeTiles.placeGround(new Soil());

        assertFull(freeTiles);
    }

    @Test
    void reserveRejectsATileFromAnotherMap() {
        GameMap farm = TestFarm.map("..");
        GameMap mines = TestFarm.map("..");
        FreeTiles freeFarmTiles = new FreeTiles(farm, randomUtils, areaFactory);

        try {
            freeFarmTiles.reserve(mines.at(0, 0));
            fail("Expected a tile from another map to be rejected");
        } catch (IllegalArgumentException expected) {
            // The tile belongs to the mines, so the farm's free tiles refused it.
        }
    }

    @Test
    void reserveAroundRejectsATileFromAnotherMap() {
        GameMap farm = TestFarm.map("..");
        GameMap mines = TestFarm.map("..");
        FreeTiles freeFarmTiles = new FreeTiles(farm, randomUtils, areaFactory);

        try {
            freeFarmTiles.reserveAround(mines.at(1, 0));
            fail("Expected a tile from another map to be rejected");
        } catch (IllegalArgumentException expected) {
            // The tile belongs to the mines, so the farm's free tiles refused it.
        }
    }

    @Test
    void eachMapKeepsItsOwnTakenTiles() {
        GameMap farm = TestFarm.map(".");
        GameMap mines = TestFarm.map(".");
        FreeTiles freeFarmTiles = new FreeTiles(farm, randomUtils, areaFactory);
        FreeTiles freeMinesTiles = new FreeTiles(mines, randomUtils, areaFactory);
        freeFarmTiles.reserve(farm.at(0, 0));

        freeMinesTiles.placeGround(new Soil());

        assertEquals(Soil.class, mines.at(0, 0).getGround().getClass(), "the full farm does not block the mines");
    }

    @Test
    void anActorIsPlacedOnTheOnlyFreeTile() throws Exception {
        GameMap map = TestFarm.map("..");
        new StackQueueValleyWorld(new Display()).addGameMap(map);
        FreeTiles freeTiles = new FreeTiles(map, randomUtils, areaFactory);
        freeTiles.reserve(map.at(0, 0));
        Farmer farmer = new Farmer("Test");

        freeTiles.placeActor(farmer);

        assertSame(map.at(1, 0), map.locationOf(farmer));
        assertTrue(map.at(1, 0).containsAnActor());
    }

    /**
     * Checks that nothing more can be placed, because every tile is taken.
     *
     * @param freeTiles the free tiles that should have none left
     */
    private void assertFull(FreeTiles freeTiles) {
        try {
            freeTiles.placeGround(new Soil());
            fail("Expected placing on a full map to fail");
        } catch (IllegalStateException expected) {
            // Every tile was taken, so placing failed instead of searching forever.
        }
    }
}
