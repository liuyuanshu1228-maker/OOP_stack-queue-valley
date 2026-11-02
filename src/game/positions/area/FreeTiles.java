package game.positions.area;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.random.RandomUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * The tiles of one map that are still free while the game world is being set
 * up, and the one rule for using them: something placed at random goes on a
 * random free tile, and that tile is then taken.
 * <p>
 * Without this class, the world passed the map, a bare set of taken tiles,
 * the RandomUtils and the AreaFactory together through every set-up method
 * (a Data Clump), and any of those methods could change the set directly.
 * The taken tiles are now private to this class and change only through its
 * methods. Each map has its own FreeTiles, which rejects a tile from any
 * other map, so the farm's taken tiles cannot be mixed up with the mines'.
 * When no free tile is left, placing fails with an exception instead of
 * searching forever.
 */
public class FreeTiles {

    /** The map whose tiles this tracks. */
    private final GameMap map;
    /** The randomness used to pick a tile. */
    private final RandomUtils randomUtils;
    /** Builds the Area of a tile and its neighbours. */
    private final AreaFactory areaFactory;
    /** The tiles already used or kept clear. */
    private final Set<Location> takenTiles = new HashSet<>();

    /**
     * Creates the free tiles of a map, with every tile free.
     *
     * @param map the map whose tiles this tracks
     * @param randomUtils the randomness used to pick a tile
     * @param areaFactory builds the Area of a tile and its neighbours
     */
    public FreeTiles(GameMap map, RandomUtils randomUtils, AreaFactory areaFactory) {
        this.map = map;
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * Marks a tile as taken, so nothing is placed on it at random.
     *
     * @param tile the tile to keep clear
     * @throws IllegalArgumentException if the tile is not on this map
     */
    public void reserve(Location tile) {
        requireOnThisMap(tile);
        takenTiles.add(tile);
    }

    /**
     * Marks a tile and every tile next to it as taken.
     *
     * @param centre the tile to keep clear, together with its neighbours
     * @throws IllegalArgumentException if the tile is not on this map
     */
    public void reserveAround(Location centre) {
        requireOnThisMap(centre);
        takenTiles.addAll(areaFactory.centredOn(centre).tiles());
    }

    /**
     * Puts a ground on a random free tile, which is then taken.
     *
     * @param ground the ground to place
     * @throws IllegalStateException if no free tile is left
     */
    public void placeGround(Ground ground) {
        claimRandomFreeTile().setGround(ground);
    }

    /**
     * Puts an item on a random free tile, which is then taken.
     *
     * @param item the item to place
     * @throws IllegalStateException if no free tile is left
     */
    public void placeItem(Item item) {
        claimRandomFreeTile().addItem(item);
    }

    /**
     * Puts an actor on a random free tile, which is then taken.
     *
     * @param actor the actor to place
     * @throws GameEngineException if the engine cannot add the actor to the chosen tile
     * @throws IllegalStateException if no free tile is left
     */
    public void placeActor(Actor actor) throws GameEngineException {
        claimRandomFreeTile().addActor(actor);
    }

    /**
     * Picks a random tile that is not taken, and marks it as taken.
     *
     * @return the chosen tile
     * @throws IllegalStateException if every tile of the map is already taken
     */
    private Location claimRandomFreeTile() {
        if (takenTiles.size() >= tileCount()) {
            throw new IllegalStateException("No free tile is left to place something on");
        }
        Location tile;
        do {
            tile = randomTile();
        } while (takenTiles.contains(tile));
        takenTiles.add(tile);
        return tile;
    }

    /**
     * Rejects a tile that belongs to a different map, so the taken tiles only
     * ever hold tiles of this map.
     *
     * @param tile the tile to check
     * @throws IllegalArgumentException if the tile is not on this map
     */
    private void requireOnThisMap(Location tile) {
        if (tile.map() != map) {
            throw new IllegalArgumentException("The tile " + tile + " is not on this map");
        }
    }

    /**
     * Counts every tile of the map.
     *
     * @return the number of tiles on the map
     */
    private int tileCount() {
        int width = map.getXRange().max() - map.getXRange().min() + 1;
        int height = map.getYRange().max() - map.getYRange().min() + 1;
        return width * height;
    }

    /**
     * Picks any tile of the map at random.
     *
     * @return a uniformly random tile of the map
     */
    private Location randomTile() {
        int x = randomUtils.randomInt(map.getXRange().max() + 1);
        int y = randomUtils.randomInt(map.getYRange().max() + 1);
        return map.at(x, y);
    }
}
