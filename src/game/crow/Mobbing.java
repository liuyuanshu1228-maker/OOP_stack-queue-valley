package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.CreatureState;
import game.positions.LocationUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * The crow has spotted a lone actor and moves in to harass them. Each turn
 * it closes the gap; once adjacent it steals one item from the target's
 * inventory and tucks it into its own, so the stolen goods are dropped on
 * the spot if the crow is later forced into Fleeing.
 * <p>
 * Transitions out:
 * <ul>
 *   <li>→ {@link Fleeing} when the area becomes crowded (2+ other actors)</li>
 *   <li>→ {@link Perching} when no actors remain within sensing range</li>
 * </ul>
 */
public final class Mobbing implements CreatureState {

    /** The single shared instance of this stateless state. */
    public static final Mobbing INSTANCE = new Mobbing();

    private static final Random RANDOM = new Random();

    private Mobbing() {
    }

    /**
     * Steps toward the nearest visible actor and, once adjacent, steals one
     * random item from their inventory into the crow's own.
     *
     * @param creature the crow acting
     * @param map the map the crow is on
     * @return a description of what happened
     */
    @Override
    public String act(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        Optional<Location> targetLoc = LocationUtils.nearestWhere(
                here, Crow.SENSING_RADIUS, Location::containsAnActor);

        if (targetLoc.isEmpty()) {
            return creature + " mobs the area but finds no target.";
        }

        Location target = targetLoc.get();

        if (LocationUtils.chebyshevDistance(here, target) == 1) {
            return steal(creature, target.getActor());
        }

        Optional<Location> step = LocationUtils.stepToward(here, target, creature);
        if (step.isEmpty()) {
            return creature + " cannot get closer to mob.";
        }
        map.moveActor(creature, step.get());
        return creature + " swoops toward " + target.getActor() + " to mob them.";
    }

    /**
     * Takes one random item from {@code victim}'s inventory and places it in
     * the crow's inventory instead.
     *
     * @param creature the crow doing the stealing
     * @param victim the actor being robbed
     * @return a description of what was taken, or a message if nothing could be stolen
     */
    private String steal(Actor creature, Actor victim) {
        List<Item> victimItems = victim.getInventory().getItems();
        if (victimItems.isEmpty()) {
            return creature + " harasses " + victim + " but finds nothing to steal.";
        }
        Item stolen = victimItems.get(RANDOM.nextInt(victimItems.size()));
        victim.getInventory().remove(stolen);
        creature.getInventory().add(stolen);
        return creature + " steals " + stolen + " from " + victim + "!";
    }

    /**
     * Panics into {@link Fleeing} when the area becomes crowded; settles
     * back into {@link Perching} once all actors have gone; otherwise stays.
     *
     * @param creature the crow whose next state is being decided
     * @param map the map the crow is on
     * @return the next state
     */
    @Override
    public CreatureState nextState(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);

        if (CrowSensing.isCrowded(here)) {
            return Fleeing.INSTANCE;
        }
        if (CrowSensing.countActorsWithinRange(here) == 0) {
            return Perching.INSTANCE;
        }
        return this;
    }

    /**
     * The crow announces its attack as it dives toward its lone target.
     *
     * @param creature the crow entering this state
     * @param map the map the crow is on
     * @return a description of the entry effect
     */
    @Override
    public String onEnter(Actor creature, GameMap map) {
        return creature + " spots a lone target and dives in to mob!";
    }

    @Override
    public String toString() {
        return "Mobbing";
    }
}
