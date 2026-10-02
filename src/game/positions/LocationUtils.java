package game.positions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Utility methods for working with the tiles around a given Location.
 */
public final class LocationUtils {

    private static final Random RANDOM = new Random();

    private LocationUtils() {
        // Prevent instantiation; this class only provides static utility methods.
    }

    /**
     * Places the given Item on a randomly selected tile adjacent to the given Location.
     * Does nothing if no adjacent tile exists.
     *
     * @param referenceLocation the Location whose adjacent tiles are considered
     * @param item the Item to place
     * @param random the Random instance to use for tile selection
     */
    public static void dropItemNearby(Location referenceLocation, Item item, Random random) {
        List<Location> nearby = referenceLocation.getNearbyLocations(1);
        if (!nearby.isEmpty()) {
            nearby.get(random.nextInt(nearby.size())).addItem(item);
        }
    }

    /**
     * Places the given Item on a randomly selected tile adjacent to the given Location,
     * using this class's own Random instance. Does nothing if no adjacent tile exists.
     *
     * @param referenceLocation the Location whose adjacent tiles are considered
     * @param item the Item to place
     */
    public static void dropItemNearby(Location referenceLocation, Item item) {
        dropItemNearby(referenceLocation, item, RANDOM);
    }

    /**
     * Places a new Item on every tile adjacent to the given Location (but not
     * on the Location itself). Each tile gets its own Item, created by the
     * given factory.
     *
     * @param referenceLocation the Location whose adjacent tiles receive an Item
     * @param itemFactory a supplier that creates a new instance of the Item to place
     */
    public static void addItemToEachNearby(Location referenceLocation, Supplier<? extends Item> itemFactory) {
        for (Location nearby : referenceLocation.getNearbyLocations(1)) {
            nearby.addItem(itemFactory.get());
        }
    }

    /**
     * Damages the Actor (if any) on the given Location and on each tile
     * adjacent to it.
     *
     * @param centre the Location at the centre of the blast
     * @param damage the damage to deal to each Actor caught in it
     */
    public static void hurtActorsAround(Location centre, int damage) {
        List<Location> blastLocations = new ArrayList<>(centre.getNearbyLocations(1));
        blastLocations.add(centre);
        for (Location blastLocation : blastLocations) {
            if (blastLocation.containsAnActor()) {
                blastLocation.getActor().hurt(damage);
            }
        }
    }

    /**
     * Picks a random tile adjacent to the given Location that the given Actor
     * is able to enter.
     *
     * @param referenceLocation the Location whose adjacent tiles are considered
     * @param actor the Actor who would move onto the chosen tile
     * @return a random enterable adjacent tile, or empty if there is none
     */
    public static Optional<Location> randomEnterableNearby(Location referenceLocation, Actor actor) {
        List<Location> candidates = new ArrayList<>();
        for (Location nearby : referenceLocation.getNearbyLocations(1)) {
            if (nearby.canActorEnter(actor)) {
                candidates.add(nearby);
            }
        }
        return pickOne(candidates);
    }

    /**
     * Picks a random tile anywhere on the given map that the given Actor is
     * able to enter.
     *
     * @param map the map to pick a tile on
     * @param actor the Actor who would move onto the chosen tile
     * @return a random enterable tile, or empty if there is none
     */
    public static Optional<Location> randomEnterableLocation(GameMap map, Actor actor) {
        List<Location> candidates = new ArrayList<>();
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location location = map.at(x, y);
                if (location.canActorEnter(actor)) {
                    candidates.add(location);
                }
            }
        }
        return pickOne(candidates);
    }

    /**
     * Picks one of the given options uniformly at random.
     *
     * @param options the options to pick from
     * @return one randomly chosen option, or empty if there are no options
     */
    private static Optional<Location> pickOne(List<Location> options) {
        if (options.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(options.get(RANDOM.nextInt(options.size())));
    }

    /**
     * The distance between two Locations on the same map, measured as the
     * number of tile-steps a king would need on a chessboard (the same
     * metric {@link Location#getNearbyLocations} uses for its square radius).
     *
     * @param a the first Location
     * @param b the second Location
     * @return the Chebyshev distance between a and b
     */
    public static int chebyshevDistance(Location a, Location b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }

    /**
     * Finds the closest Location to referenceLocation, within the given
     * radius, that satisfies the given condition.
     *
     * @param referenceLocation the Location to search outward from
     * @param radius the maximum distance to search
     * @param condition the condition a candidate Location must satisfy
     * @return the closest matching Location, or empty if none match
     */
    public static Optional<Location> nearestWhere(Location referenceLocation, int radius, Predicate<Location> condition) {
        Location nearest = null;
        int nearestDistance = Integer.MAX_VALUE;

        for (Location candidate : referenceLocation.getNearbyLocations(radius)) {
            if (!condition.test(candidate)) {
                continue;
            }
            int distance = chebyshevDistance(referenceLocation, candidate);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = candidate;
            }
        }

        return Optional.ofNullable(nearest);
    }

    /**
     * Picks the tile adjacent to from (that actor could step onto) which
     * brings actor closest to target, by Chebyshev distance.
     *
     * @param from the Location to step from
     * @param target the Location being approached
     * @param actor the Actor that would be doing the stepping
     * @return the best adjacent Location to step to, or empty if no adjacent
     * tile is enterable or gets any closer than staying put
     */
    public static Optional<Location> stepToward(Location from, Location target, Actor actor) {
        Location best = null;
        int bestDistance = chebyshevDistance(from, target);

        for (Location candidate : from.getNearbyLocations(1)) {
            if (!candidate.canActorEnter(actor)) {
                continue;
            }
            int distance = chebyshevDistance(candidate, target);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = candidate;
            }
        }

        return Optional.ofNullable(best);
    }
}