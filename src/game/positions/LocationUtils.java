package game.positions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
}