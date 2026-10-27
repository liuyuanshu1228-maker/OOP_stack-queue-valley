package game.testsupport;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.farm.Dirt;
import game.positions.farm.Soil;

import java.util.Arrays;

/**
 * Builds small maps for tests, where '.' is Dirt and ',' is Soil.
 */
public final class TestFarm {

    /** The name of every test map, which appears in the plants' messages. */
    public static final String MAP_NAME = "Farm";

    private TestFarm() {
    }

    /**
     * Builds a map from the given rows.
     *
     * @param rows the rows of the map, top to bottom
     * @return the new map
     */
    public static GameMap map(String... rows) {
        try {
            DefaultGroundCreator groundCreator = new DefaultGroundCreator();
            groundCreator.registerGround('.', Dirt::new);
            groundCreator.registerGround(',', Soil::new);
            return new GameMap(MAP_NAME, groundCreator, Arrays.asList(rows));
        } catch (GameEngineException e) {
            throw new IllegalStateException("Could not build the test map", e);
        }
    }

    /**
     * Ticks a location the given number of times, as the game would do once
     * per turn.
     *
     * @param location the location to tick
     * @param turns how many turns to tick it for
     */
    public static void tick(Location location, int turns) {
        for (int i = 0; i < turns; i++) {
            location.tick();
        }
    }

    /**
     * Counts the tiles whose ground is exactly the given class.
     *
     * @param map the map to search
     * @param groundClass the class of ground to count
     * @return how many tiles have that ground
     */
    public static int countGround(GameMap map, Class<?> groundClass) {
        int count = 0;
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                if (map.at(x, y).getGround().getClass() == groundClass) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Counts the items lying on the whole map.
     *
     * @param map the map to search
     * @return how many items are on the map
     */
    public static int countItems(GameMap map) {
        int count = 0;
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                count += map.at(x, y).getItems().size();
            }
        }
        return count;
    }
}
