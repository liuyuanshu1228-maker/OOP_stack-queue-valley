package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.creaturestate.BaseCreatureState;
import game.creaturestate.CreatureState;
import game.creaturestate.ItemCache;
import game.positions.area.Area;
import game.positions.area.BareOfItems;
import game.positions.area.ItemDropper;
import game.positions.area.OutsideOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The crow seeks out the nearest item lying on the ground and, once it
 * reaches it, carries it off to a hidden spot instead of eating it on the
 * spot.
 */
public class Foraging extends BaseCreatureState {

    /** How far around a found item the crow searches for a hiding spot. */
    private static final int CACHE_SEARCH_RADIUS = 4;

    /** How far the hiding spot must be from where the item was found. */
    private static final int MIN_CACHE_DISTANCE = 2;

    /**
     * Creates this state. A new instance is created every time a transition
     * returns this state; {@link BaseCreatureState} gives every instance of
     * the same class the same identity, so creating a fresh one each time
     * costs nothing.
     */
    public Foraging() {
    }

    /**
     * If standing over an item, caches it; otherwise takes one step toward
     * the nearest item within sensing range.
     *
     * @param creature the crow acting
     * @param map the map the crow is on
     * @return a description of what happened
     */
    @Override
    public String act(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        CrowSensing sensing = new CrowSensing(creature, here);

        if (sensing.isOnFood()) {
            return cache(creature, here);
        }

        Optional<Location> itemLocation = sensing.nearestFood();
        if (itemLocation.isEmpty()) {
            return creature + " finds nothing worth foraging.";
        }

        Optional<Location> step = new CrowNavigation(creature, here).stepToward(itemLocation.get());
        if (step.isEmpty()) {
            return creature + " can't find a way toward the food.";
        }

        map.moveActor(creature, step.get());
        return creature + " hops toward the food at " + itemLocation.get()
                + ", landing at " + step.get() + ".";
    }

    /**
     * Picks up the item at here and hides it on an empty tile at least
     * {@value #MIN_CACHE_DISTANCE} tiles away, if one can be found.
     *
     * @param creature the crow doing the caching
     * @param here the Location the item is being picked up from
     * @return a description of what happened
     */
    private String cache(Actor creature, Location here) {
        Optional<Item> forageable = new CrowSensing(creature, here).forageableItemHere();
        if (forageable.isEmpty()) {
            throw new IllegalStateException("cache() called with nothing forageable at " + here);
        }
        Item item = forageable.get();
        Optional<Location> chosenSpot = Area.within(here, CACHE_SEARCH_RADIUS)
                .filter(new OutsideOf(Area.within(here, MIN_CACHE_DISTANCE - 1)))
                .filter(new BareOfItems())
                .randomTile();

        if (chosenSpot.isEmpty()) {
            return creature + " spots the " + item + " at " + here
                    + ", but can't find anywhere to hide it.";
        }

        Location hidingSpot = chosenSpot.get();
        here.removeItem(item);
        hidingSpot.addItem(item);
        Optional<ItemCache> cache = creature.asCapability(ItemCache.class);
        if (cache.isPresent()) {
            cache.get().remember(item);
        }
        return creature + " snatches the " + item + " from " + here
                + " and hides it at " + hidingSpot + ".";
    }

    /**
     * Moves to Fleeing if the crow feels crowded (see
     * {@link CrowSensing#isCrowded()}), or to Perching once there is nothing
     * left to forage within sensing range. The condition for moving to
     * Mobbing is added once that state exists.
     *
     * @param creature the crow whose next state is being decided
     * @param map the map the crow is on
     * @return Fleeing if crowded, Perching if nothing is left to forage, otherwise this state
     */
    @Override
    public CreatureState nextState(Actor creature, GameMap map) {
        CrowSensing sensing = new CrowSensing(creature, map.locationOf(creature));
        if (sensing.isCrowded()) {
            return new Fleeing();
        }

        boolean foodNearby = sensing.foodWithinRange();

        if (!foodNearby) {
            return new Perching();
        }

        return this;
    }

    /**
     * The effect of swooping in on a found item: every other item within 1
     * tile of it is scattered onto a random nearby tile, as if startled by
     * the sudden arrival.
     *
     * @param creature the crow entering this state
     * @param map the map the crow is on
     * @return a description of the effect
     */
    @Override
    public String onEnter(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        Optional<Location> itemLocation = new CrowSensing(creature, here).nearestFood();

        if (itemLocation.isEmpty()) {
            return creature + " starts foraging the items at " + here
                    + ", without swooping anywhere, so no items are scattered.";
        }

        List<String> scattered = scatterNearbyItems(itemLocation.get());
        if (scattered.isEmpty()) {
            return creature + " swoops down toward the food at " + itemLocation.get()
                    + ", but there are no other items around it to scatter.";
        }
        return creature + " swoops down toward the food at " + itemLocation.get()
                + ", scattering the other items around it: " + scattered + ".";
    }

    /**
     * Relocates every item within 1 tile of centre onto a random tile
     * within the same radius. All items are lifted first, so each one is
     * moved exactly once even if it lands on a tile not yet visited.
     *
     * @param centre the Location to scatter items around
     * @return where each item landed, e.g. "Egg to (3, 4) on Inherited Farm"
     */
    private List<String> scatterNearbyItems(Location centre) {
        ItemDropper dropper = new ItemDropper(Area.adjacentTo(centre));
        List<String> scattered = new ArrayList<>();

        for (Item item : dropper.liftAll()) {
            Optional<Location> destination = dropper.dropOnRandomTile(item);
            if (destination.isPresent()) {
                scattered.add(item + " to " + destination.get());
            }
        }

        return scattered;
    }

    /** @return the name of this state */
    @Override
    public String toString() {
        return "Foraging";
    }
}
