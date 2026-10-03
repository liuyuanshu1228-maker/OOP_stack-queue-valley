package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ItemCache;
import game.positions.LocationUtils;

import java.util.Optional;

/**
 * Shared predicates used by this crow's states to query what is around it,
 * so each state's transition logic stays a short, readable expression
 * instead of repeating the same checks.
 */
final class CrowSensing {

    /** The number of actors within sensing range that makes the crow feel crowded. */
    static final int CROWD_SIZE = 2;

    private CrowSensing() {
    }

    /**
     * Counts the number of other actors (excluding the crow itself) visible
     * within {@link Crow#SENSING_RADIUS} of here.  Because
     * {@link Location#getNearbyLocations} does not include the centre tile,
     * the crow at {@code here} is never included in the count.
     *
     * @param here the Location the crow is currently on
     * @return the number of other actors within sensing range
     */
    static int countActorsWithinRange(Location here) {
        int count = 0;
        for (Location nearby : here.getNearbyLocations(Crow.SENSING_RADIUS)) {
            if (nearby.containsAnActor()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Whether there are two or more other actors within sensing range —
     * enough to make the crow feel outnumbered and panic into Fleeing.
     *
     * @param here the Location the crow is currently on
     * @return true if two or more other actors are within sensing range
     */
    static boolean isCrowded(Location here) {
        return countActorsWithinRange(here) >= 2;
    }

    /**
     * Whether there is an Item at this Location worth foraging: one that
     * the given creature hasn't already hidden away itself.
     *
     * @param location the Location to check
     * @param creature the creature doing the sensing
     * @return true if there is at least one forageable Item at this Location
     */
    static boolean hasForageableItems(Location location, Actor creature) {
        ItemCache cache = creature.asCapability(ItemCache.class).orElse(null);

        for (Item item : location.getItems()) {
            if (cache == null || !cache.remembers(item)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Whether there is any forageable Item within the crow's sensing range
     * of the given Location, including the Location itself.
     *
     * @param here the Location to sense from
     * @param creature the creature doing the sensing
     * @return true if forageable food is within sensing range
     */
    static boolean foodWithinRange(Location here, Actor creature) {
        return hasForageableItems(here, creature) || nearestFood(here, creature).isPresent();
    }

    /**
     * Finds the closest Location within sensing range (excluding here) that
     * holds a forageable Item.
     *
     * @param here the Location to sense from
     * @param creature the creature doing the sensing
     * @return the closest Location with forageable food, or empty if there is none
     */
    static Optional<Location> nearestFood(Location here, Actor creature) {
        return LocationUtils.nearestWhere(here, Crow.SENSING_RADIUS,
                candidate -> hasForageableItems(candidate, creature));
    }

    /**
     * Counts the other actors within the crow's sensing range of the given Location.
     *
     * @param here the Location to sense from
     * @return the number of actors within sensing range, not counting one standing on here
     */
    static int countActorsWithinRange(Location here) {
        int count = 0;
        for (Location location : here.getNearbyLocations(Crow.SENSING_RADIUS)) {
            if (location.containsAnActor()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Whether enough actors are within sensing range for the crow to feel crowded.
     *
     * @param here the Location to sense from
     * @return true if at least {@value #CROWD_SIZE} actors are within sensing range
     */
    static boolean isCrowded(Location here) {
        return countActorsWithinRange(here) >= CROWD_SIZE;
    }

    /**
     * Finds the closest Location within sensing range (excluding here) that holds an actor.
     *
     * @param here the Location to sense from
     * @return the closest Location holding an actor, or empty if there is none
     */
    static Optional<Location> nearestActor(Location here) {
        return LocationUtils.nearestWhere(here, Crow.SENSING_RADIUS, Location::containsAnActor);
    }
}