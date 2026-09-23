package game;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;
import java.util.Random;

/**
 * Utility methods for placing Items near a given Location.
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
     * @param item              the Item to place
     * @param random            the Random instance to use for tile selection
     */
    public static void dropItemNearby(Location referenceLocation, Item item, Random random) {
        List<Location> nearby = referenceLocation.getNearbyLocations(1);
        if (!nearby.isEmpty()) {
            nearby.get(random.nextInt(nearby.size())).addItem(item);
        }
    }
}
