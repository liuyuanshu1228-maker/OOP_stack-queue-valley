package game.positions.area;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A group of tiles on a map, and the choosing of tiles from it.
 * <p>
 * Any shape of area can be built directly with the constructor; the static
 * factories only name the shapes the game already uses. What is done to the
 * tiles of an area (placing Items, hurting Actors, ...) is the job of other
 * classes, such as {@link ItemDropper} and {@link Explosion}, so that this
 * class only changes when the way tiles are chosen changes.
 * <p>
 * Contract for subclasses: {@link #randomTile()} and
 * {@link #randomEnterableTile(Actor)} must only ever return a tile that is
 * one of {@link #tiles()}.
 */
public class Area {

    /** How far from a centre tile an "adjacent" tile may be. */
    private static final int ADJACENT_RADIUS = 1;

    /** The tiles of this area. */
    private final List<Location> tiles;

    /**
     * Creates an area made of the given tiles.
     *
     * @param tiles the tiles that make up the area
     */
    public Area(List<Location> tiles) {
        this.tiles = new ArrayList<>(tiles);
    }

    /**
     * Creates the area of tiles adjacent to the given Location, not including it.
     *
     * @param centre the Location whose adjacent tiles form the area
     * @return the adjacent tiles
     */
    public static Area adjacentTo(Location centre) {
        return new Area(centre.getNearbyLocations(ADJACENT_RADIUS));
    }

    /**
     * Creates the area made of the given Location together with its adjacent tiles.
     *
     * @param centre the Location at the centre of the area
     * @return the centre and its adjacent tiles
     */
    public static Area centredOn(Location centre) {
        List<Location> centreAndAdjacent = new ArrayList<>(centre.getNearbyLocations(ADJACENT_RADIUS));
        centreAndAdjacent.add(centre);
        return new Area(centreAndAdjacent);
    }

    /**
     * Creates the area made of every tile on the given map.
     *
     * @param map the map whose tiles form the area
     * @return every tile of the map
     */
    public static Area wholeMap(GameMap map) {
        List<Location> allTiles = new ArrayList<>();
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                allTiles.add(map.at(x, y));
            }
        }
        return new Area(allTiles);
    }

    /**
     * @return the tiles of this area, as a read-only list
     */
    public List<Location> tiles() {
        return Collections.unmodifiableList(tiles);
    }

    /**
     * Picks one tile of this area uniformly at random.
     *
     * @return a random tile of this area, or empty if the area has no tiles
     */
    public Optional<Location> randomTile() {
        if (tiles.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(RandomUtils.pickRandom(tiles));
    }

    /**
     * Picks, uniformly at random, one tile of this area that the given Actor is able to enter.
     *
     * @param actor the Actor who would move onto the chosen tile
     * @return a random enterable tile of this area, or empty if there is none
     */
    public Optional<Location> randomEnterableTile(Actor actor) {
        List<Location> enterableTiles = new ArrayList<>();
        for (Location tile : tiles) {
            if (tile.canActorEnter(actor)) {
                enterableTiles.add(tile);
            }
        }
        return new Area(enterableTiles).randomTile();
    }
}
