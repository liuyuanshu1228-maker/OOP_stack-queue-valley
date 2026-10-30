package game.positions.area;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.random.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds the Areas the game uses: the named shapes it already relies on
 * (within a radius, adjacent, centred on a tile, the whole map), plus
 * {@link #of(List)} for any custom list of tiles a caller has put together
 * itself. Every Area this factory builds carries the same RandomUtils this
 * factory was given, so random tile choices made on it share one source of
 * randomness with the rest of the game.
 */
public class AreaFactory {

    /** How far from a centre tile an "adjacent" tile may be. */
    private static final int ADJACENT_RADIUS = 1;

    /** The randomness given to every Area this factory builds. */
    private final RandomUtils randomUtils;

    /**
     * Creates a factory that builds Areas using the given randomness.
     *
     * @param randomUtils the randomness given to every Area this factory builds
     */
    public AreaFactory(RandomUtils randomUtils) {
        this.randomUtils = randomUtils;
    }

    /**
     * Builds an Area out of a tile list a caller has already put together
     * itself (e.g. a hand-picked set of candidate steps), carrying this
     * factory's randomness.
     *
     * @param tiles the tiles that make up the area
     * @return the area made of those tiles
     */
    public Area of(List<Location> tiles) {
        return new Area(tiles, randomUtils);
    }

    /**
     * Builds the area of tiles within the given distance of a Location, not
     * including the Location itself. Distance is measured as a king moves on a
     * chessboard, so the area is a square around the centre.
     *
     * @param centre the Location at the middle of the area
     * @param radius how many tiles away from the centre the area reaches
     * @return the tiles within the radius
     */
    public Area within(Location centre, int radius) {
        return of(centre.getNearbyLocations(radius));
    }

    /**
     * Builds the area of tiles adjacent to the given Location, not including it.
     *
     * @param centre the Location whose adjacent tiles form the area
     * @return the adjacent tiles
     */
    public Area adjacentTo(Location centre) {
        return within(centre, ADJACENT_RADIUS);
    }

    /**
     * Builds the area made of the given Location together with its adjacent tiles.
     *
     * @param centre the Location at the centre of the area
     * @return the centre and its adjacent tiles
     */
    public Area centredOn(Location centre) {
        List<Location> centreAndAdjacent = new ArrayList<>(adjacentTo(centre).tiles());
        centreAndAdjacent.add(centre);
        return of(centreAndAdjacent);
    }

    /**
     * Builds the area made of every tile on the given map.
     *
     * @param map the map whose tiles form the area
     * @return every tile of the map
     */
    public Area wholeMap(GameMap map) {
        List<Location> allTiles = new ArrayList<>();
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                allTiles.add(map.at(x, y));
            }
        }
        return of(allTiles);
    }
}
