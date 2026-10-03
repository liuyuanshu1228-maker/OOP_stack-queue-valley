package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Composure;
import game.actors.CreatureState;
import game.positions.LocationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The crow panics and keeps moving away from the closest actor until it has
 * felt safe for long enough to calm down.
 * <p>
 * "Safe" means no actor is within the crow's sensing range. The crow
 * remembers how many turns in a row it has been safe through its
 * {@link Composure} capability, since this state is shared between crows.
 */
public final class Fleeing implements CreatureState {

    /** The single shared instance of this stateless state. */
    public static final Fleeing INSTANCE = new Fleeing();

    /** How many consecutive safe turns it takes for the crow to fully calm down. */
    private static final int CALM_DOWN_TURNS = 3;

    private Fleeing() {
    }

    /**
     * Takes one step directly away from the closest actor within sensing range,
     * or stays put if it is cornered or nothing is nearby.
     *
     * @param creature the crow acting
     * @param map the map the crow is on
     * @return a description of what happened
     */
    @Override
    public String act(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        Optional<Location> threat = CrowSensing.nearestActor(here);
        if (threat.isEmpty()) {
            return creature + " keeps a wary eye out, catching its breath.";
        }

        Optional<Location> escape = LocationUtils.stepAway(here, threat.get(), creature);
        if (escape.isEmpty()) {
            return creature + " is cornered by " + threat.get().getActor() + " and flaps in place.";
        }
        map.moveActor(creature, escape.get());
        return creature + " flees from " + threat.get().getActor() + ".";
    }

    /**
     * Updates how long the crow has felt safe, then decides where to go next:
     * Perching once it has been safe for {@value #CALM_DOWN_TURNS} turns in a row,
     * Foraging if it is safe but not yet calm and food is within range,
     * otherwise it keeps fleeing.
     *
     * @param creature the crow whose next state is being decided
     * @param map the map the crow is on
     * @return the state the crow should be in next
     */
    @Override
    public CreatureState nextState(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        boolean safe = CrowSensing.countActorsWithinRange(here) == 0;
        int safeTurns = updateComposure(creature, safe);

        if (safe && safeTurns >= CALM_DOWN_TURNS) {
            return Perching.INSTANCE;
        }
        if (safe && CrowSensing.foodWithinRange(here, creature)) {
            return Foraging.INSTANCE;
        }
        return this;
    }

    /**
     * The effect of panicking: the crow drops everything it is carrying onto
     * random nearby tiles, and the beating of its wings startles every actor
     * next to it one step backwards.
     *
     * @param creature the crow entering this state
     * @param map the map the crow is on
     * @return a description of the effect
     */
    @Override
    public String onEnter(Actor creature, GameMap map) {
        creature.asCapability(Composure.class).ifPresent(Composure::resetComposure);
        Location here = map.locationOf(creature);

        int dropped = dropEverything(creature, here);
        int startled = startleNeighbours(here, map);

        String message = creature + " panics and takes flight";
        if (dropped > 0) {
            message += ", dropping " + dropped + " item(s)";
        }
        if (startled > 0) {
            message += ", startling " + startled + " actor(s) backwards";
        }
        return message + "!";
    }

    /**
     * Records whether the crow felt safe this turn and returns its updated
     * count of consecutive safe turns.
     *
     * @param creature the crow being updated
     * @param safe whether no actor is within sensing range this turn
     * @return the number of consecutive safe turns, or 0 if the crow cannot track it
     */
    private int updateComposure(Actor creature, boolean safe) {
        Optional<Composure> composure = creature.asCapability(Composure.class);
        if (composure.isEmpty()) {
            return 0;
        }
        if (safe) {
            composure.get().recordSafeTurn();
        } else {
            composure.get().resetComposure();
        }
        return composure.get().getSafeTurns();
    }

    /**
     * Drops every item the crow is carrying onto random tiles next to it.
     *
     * @param creature the crow dropping its items
     * @param here the crow's current Location
     * @return the number of items dropped
     */
    private int dropEverything(Actor creature, Location here) {
        List<Item> carried = new ArrayList<>(creature.getInventory().getItems());
        for (Item item : carried) {
            creature.getInventory().remove(item);
            LocationUtils.dropItemNearby(here, item);
        }
        return carried.size();
    }

    /**
     * Pushes every actor adjacent to here one step further away from it, if
     * there is an enterable tile for them to step back onto.
     *
     * @param here the Location the actors are pushed away from
     * @param map the map the actors are on
     * @return the number of actors pushed back
     */
    private int startleNeighbours(Location here, GameMap map) {
        int startled = 0;
        for (Location neighbour : here.getNearbyLocations(1)) {
            if (!neighbour.containsAnActor()) {
                continue;
            }
            Actor actor = neighbour.getActor();
            Optional<Location> backStep = LocationUtils.stepAway(neighbour, here, actor);
            if (backStep.isPresent()) {
                map.moveActor(actor, backStep.get());
                startled++;
            }
        }
        return startled;
    }

    @Override
    public String toString() {
        return "Fleeing";
    }
}
