package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.CreatureState;
import game.positions.LocationUtils;
import game.util.RandomUtils;

import java.util.List;
import java.util.Optional;

/**
 * The crow has spotted a lone actor and dives in to harass them. On entering
 * this state the crow immediately grabs an item from the target regardless of
 * distance (a surprise first strike). Each subsequent turn it closes the gap
 * and steals again once adjacent. Stolen items sit in the crow's own inventory
 * and are dropped when it later panics into {@link Fleeing}, letting the
 * Farmer pick them back up.
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
        Optional<Location> targetLoc = CrowSensing.nearestActor(here);

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
        Item stolen = RandomUtils.pickRandom(victimItems);
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
     * First-strike: the crow dives and immediately steals one item from the
     * nearest visible actor, regardless of distance. This is the only place
     * in the codebase where an item is taken from a live actor without the
     * actor needing to be adjacent first.
     *
     * @param creature the crow entering this state
     * @param map the map the crow is on
     * @return a description of the ambush and what (if anything) was stolen
     */
    @Override
    public String onEnter(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        Optional<Location> targetLoc = CrowSensing.nearestActor(here);

        if (targetLoc.isEmpty()) {
            return creature + " swoops into an aggressive stance.";
        }

        Actor target = targetLoc.get().getActor();
        return creature + " dives aggressively at " + target + "! " + steal(creature, target);
    }

    /** @return the name of this state */
    @Override
    public String toString() {
        return "Mobbing";
    }
}
