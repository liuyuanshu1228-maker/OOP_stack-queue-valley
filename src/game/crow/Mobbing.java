package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.creaturestate.CreatureState;
import game.positions.area.Area;
import game.positions.area.ItemDropper;
import game.RandomUtils;

import java.util.List;
import java.util.Optional;

/**
 * The crow has spotted a lone actor and dives in to harass them. On entering
 * this state the crow's dive knocks everything the target is carrying onto
 * the tiles around them. Each subsequent turn it closes the gap and, once
 * adjacent, steals one item. Stolen items sit in the crow's own inventory
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

        if (CrowNavigation.chebyshevDistance(here, target) == 1) {
            return steal(creature, target.getActor());
        }

        Optional<Location> step = CrowNavigation.stepToward(here, target, creature);
        if (step.isEmpty()) {
            return creature + " cannot get closer to mob.";
        }
        map.moveActor(creature, step.get());
        return creature + " swoops toward " + target.getActor() + " at " + target + " to mob them, landing at "
                + step.get() + ".";
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
        return creature + " steals " + stolen + " from " + victim + "! It now carries: "
                + creature.getInventory().getItems() + ".";
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
     * The crow dives at the nearest visible actor, knocking every item they
     * are carrying onto random tiles adjacent to them. Unlike stealing, the
     * crow keeps none of these items.
     *
     * @param creature the crow entering this state
     * @param map the map the crow is on
     * @return a description of the dive, listing every item knocked loose
     */
    @Override
    public String onEnter(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        Optional<Location> targetLoc = CrowSensing.nearestActor(here);

        if (targetLoc.isEmpty()) {
            return creature + " swoops into an aggressive stance.";
        }

        Actor target = targetLoc.get().getActor();
        List<Item> knockedLoose = new ItemDropper(Area.adjacentTo(targetLoc.get())).dropAllCarriedBy(target);

        if (knockedLoose.isEmpty()) {
            return creature + " dives aggressively at " + target + ", but " + target
                    + " is carrying nothing to knock loose.";
        }
        return creature + " dives aggressively at " + target + ", knocking everything they carry onto the tiles around "
                + targetLoc.get() + ": " + knockedLoose + "!";
    }

    /** @return the name of this state */
    @Override
    public String toString() {
        return "Mobbing";
    }
}
