package game.positions.area;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.random.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A group of tiles on a map, and the choosing of tiles from it.
 * <p>
 * An Area is built by an {@link AreaFactory}, which names the shapes the game
 * already uses and gives every Area it builds the same RandomUtils, so that
 * {@link #randomTile()} shares one source of randomness with the rest of the
 * game. What is done to the tiles of an area (placing Items, hurting Actors,
 * ...) is the job of other classes, such as {@link ItemDropper} and
 * {@link Explosion}, so that this class only changes when the way tiles are
 * chosen changes.
 * <p>
 * Contract for subclasses: every method that chooses a tile
 * ({@link #randomTile()}, {@link #randomEnterableTile(Actor)},
 * {@link #nearestTo(Location)} and {@link #farthestFrom(Location)}) must only
 * ever return a tile that is one of {@link #tiles()}.
 */
public class Area {

    /** Direction for {@link #firstAtExtremeDistance}: the greatest distance wins. */
    private static final int FARTHEST = 1;

    /** Direction for {@link #firstAtExtremeDistance}: the smallest distance wins. */
    private static final int NEAREST = -1;

    /** The tiles of this area. */
    private final List<Location> tiles;

    /** The randomness used to choose a tile of this area at random. */
    private final RandomUtils randomUtils;

    /**
     * Creates an area made of the given tiles.
     *
     * @param tiles the tiles that make up the area
     * @param randomUtils the randomness used to choose a tile at random
     */
    public Area(List<Location> tiles, RandomUtils randomUtils) {
        this.tiles = new ArrayList<>(tiles);
        this.randomUtils = randomUtils;
    }

    /**
     * @return the tiles of this area, as a read-only list
     */
    public List<Location> tiles() {
        return Collections.unmodifiableList(tiles);
    }

    /**
     * Narrows this area down to the tiles that meet a condition. The tiles
     * keep the order they have in this area.
     *
     * @param condition what a tile must satisfy to stay in the area
     * @return a new area made of the tiles that satisfy the condition
     */
    public Area filter(TileCondition condition) {
        List<Location> matching = new ArrayList<>();
        for (Location tile : tiles) {
            if (condition.isMetBy(tile)) {
                matching.add(tile);
            }
        }
        return new Area(matching, randomUtils);
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
        return Optional.of(randomUtils.pickRandom(tiles));
    }

    /**
     * Picks, uniformly at random, one tile of this area that the given Actor is able to enter.
     *
     * @param actor the Actor who would move onto the chosen tile
     * @return a random enterable tile of this area, or empty if there is none
     */
    public Optional<Location> randomEnterableTile(Actor actor) {
        return filter(new EnterableBy(actor)).randomTile();
    }

    /**
     * Picks the tile of this area that is closest to a reference Location.
     * If several tiles are equally close, the first of them in this area wins.
     *
     * @param reference the Location the distance is measured to
     * @return the closest tile, or empty if the area has no tiles
     */
    public Optional<Location> nearestTo(Location reference) {
        return firstAtExtremeDistance(reference, NEAREST);
    }

    /**
     * Picks the tile of this area that is furthest from a reference Location.
     * If several tiles are equally far, the first of them in this area wins.
     *
     * @param reference the Location the distance is measured to
     * @return the furthest tile, or empty if the area has no tiles
     */
    public Optional<Location> farthestFrom(Location reference) {
        return firstAtExtremeDistance(reference, FARTHEST);
    }

    /**
     * Finds the first tile of this area that is the nearest to, or the
     * farthest from, a reference Location.
     *
     * @param reference the Location the distance is measured to
     * @param direction {@link #FARTHEST} to look for the greatest distance, or
     *                  {@link #NEAREST} to look for the smallest
     * @return the first tile at the extreme distance, or empty if the area has no tiles
     */
    private Optional<Location> firstAtExtremeDistance(Location reference, int direction) {
        Location best = null;
        int bestScore = Integer.MIN_VALUE;
        for (Location tile : tiles) {
            int tileScore = direction * distanceBetween(tile, reference);
            if (tileScore > bestScore) {
                bestScore = tileScore;
                best = tile;
            }
        }
        return Optional.ofNullable(best);
    }

    /**
     * The distance between two Locations on the same map, measured as the
     * number of steps a king would need on a chessboard (the same measure
     * {@link Location#getNearbyLocations} uses for its square radius).
     * <p>
     * This is a pure, stateless geometric calculation with no behaviour to
     * substitute or override, so it stays a private static helper rather
     * than an instance method: there is nothing here for DIP or OCP to apply
     * to, the same way {@code Math.max} does not need to be injectable.
     *
     * @param a the first Location
     * @param b the second Location
     * @return the number of king's steps between a and b
     */
    private static int distanceBetween(Location a, Location b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }
}
