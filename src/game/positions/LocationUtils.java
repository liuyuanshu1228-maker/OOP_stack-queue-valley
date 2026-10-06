package game.positions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Utility methods for working with the tiles around a given Location.
 */
public final class LocationUtils {

    private LocationUtils() {
        // Prevent instantiation; this class only provides static utility methods.
    }

    /**
     * Places the given Item on a randomly selected tile adjacent to the given Location.
     * Does nothing if no adjacent tile exists.
     *
     * @param referenceLocation the Location whose adjacent tiles are considered
     * @param item the Item to place
     */
    public static void dropItemNearby(Location referenceLocation, Item item) {
        List<Location> nearby = referenceLocation.getNearbyLocations(1);
        if (!nearby.isEmpty()) {
            RandomUtils.pickRandom(nearby).addItem(item);
        }
    }

    /**
     * Removes every Item the given Actor is carrying and places each one on a
     * randomly selected tile adjacent to the given Location.
     *
     * @param actor the Actor whose inventory is emptied
     * @param referenceLocation the Location whose adjacent tiles receive the Items
     * @return the Items that were dropped, in the order they were carried
     */
    public static List<Item> dropInventoryNearby(Actor actor, Location referenceLocation) {
        List<Item> carried = new ArrayList<>(actor.getInventory().getItems());
        for (Item item : carried) {
            actor.getInventory().remove(item);
            dropItemNearby(referenceLocation, item);
        }
        return carried;
    }

    /**
     * The given Location together with every tile adjacent to it.
     *
     * @param centre the Location at the centre of the area
     * @return the centre and its adjacent tiles
     */
    public static List<Location> centreAndAdjacent(Location centre) {
        List<Location> area = new ArrayList<>(centre.getNearbyLocations(1));
        area.add(centre);
        return area;
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
        for (Location blastLocation : centreAndAdjacent(centre)) {
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
        return pickEnterable(referenceLocation.getNearbyLocations(1), actor);
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
        List<Location> allLocations = new ArrayList<>();
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                allLocations.add(map.at(x, y));
            }
        }
        return pickEnterable(allLocations, actor);
    }

    /**
     * Picks one of the given Locations that the given Actor is able to enter,
     * uniformly at random.
     *
     * @param options the Locations to pick from
     * @param actor the Actor who would move onto the chosen Location
     * @return one randomly chosen enterable Location, or empty if there is none
     */
    private static Optional<Location> pickEnterable(List<Location> options, Actor actor) {
        List<Location> candidates = options.stream()
                .filter(location -> location.canActorEnter(actor))
                .toList();
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(RandomUtils.pickRandom(candidates));
    }
}
