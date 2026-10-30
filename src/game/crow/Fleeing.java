package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.creaturestate.Composure;
import game.creaturestate.BaseCreatureState;
import game.creaturestate.CreatureState;
import game.positions.area.AreaFactory;
import game.positions.area.ItemDropper;

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
public class Fleeing extends BaseCreatureState {

    /** How many consecutive safe turns it takes for the crow to fully calm down. */
    private static final int CALM_DOWN_TURNS = 3;

    /**
     * Creates this state. A new instance is created every time a transition
     * returns this state; {@link BaseCreatureState} gives every instance of
     * the same class the same identity, so creating a fresh one each time
     * costs nothing.
     */
    public Fleeing() {
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
        Optional<Location> threat = new CrowSensing(creature, here, areaFactoryOf(creature)).nearestActor();
        if (threat.isEmpty()) {
            Optional<Composure> composure = creature.asCapability(Composure.class);
            int safeTurns = composure.isPresent() ? composure.get().getSafeTurns() : 0;
            return creature + " keeps a wary eye out, catching its breath (safe for "
                    + safeTurns + "/" + CALM_DOWN_TURNS + " turns before it calms down).";
        }

        Optional<Location> escape = new CrowNavigation(creature, here, areaFactoryOf(creature)).stepAway(threat.get());
        if (escape.isEmpty()) {
            return creature + " is cornered by " + threat.get().getActor() + " and flaps in place.";
        }
        map.moveActor(creature, escape.get());
        return creature + " flees from " + threat.get().getActor() + " at " + threat.get()
                + ", landing at " + escape.get() + ".";
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
        CrowSensing sensing = new CrowSensing(creature, map.locationOf(creature), areaFactoryOf(creature));
        boolean safe = sensing.isSafe();
        int safeTurns = updateComposure(creature, safe);

        if (safe && safeTurns >= CALM_DOWN_TURNS) {
            return new Perching();
        }
        if (safe && sensing.foodWithinRange()) {
            return new Foraging();
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
        Optional<Composure> composure = creature.asCapability(Composure.class);
        if (composure.isPresent()) {
            composure.get().resetComposure();
        }
        Location here = map.locationOf(creature);
        AreaFactory areaFactory = areaFactoryOf(creature);

        List<Item> dropped = new ItemDropper(areaFactory.adjacentTo(here)).dropAllCarriedBy(creature);
        List<String> startled = startleNeighbours(here, map, areaFactory);

        String droppedText = dropped.isEmpty()
                ? "it carries nothing to drop"
                : "dropping everything it carries onto the tiles around it: " + dropped;
        String startledText = startled.isEmpty()
                ? "no adjacent actor can be startled backwards"
                : "startling actors backwards: " + startled;
        return creature + " panics and takes flight at " + here
                + " (calm-down count reset to 0/" + CALM_DOWN_TURNS + "); "
                + droppedText + "; " + startledText + "!";
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
     * Pushes every actor adjacent to here one step further away from it, if
     * there is an enterable tile for them to step back onto. The AreaFactory
     * is the panicking crow's own: the neighbour being pushed is not
     * necessarily a crow, so {@link CrowNavigation} cannot obtain one from it.
     *
     * @param here the Location the actors are pushed away from
     * @param map the map the actors are on
     * @param areaFactory the panicking crow's own AreaFactory
     * @return where each actor was pushed, e.g. "Farmer Bob (100/100) to (3, 2) on Inherited Farm"
     */
    private List<String> startleNeighbours(Location here, GameMap map, AreaFactory areaFactory) {
        List<String> startled = new ArrayList<>();
        for (Location neighbour : areaFactory.adjacentTo(here).tiles()) {
            if (!neighbour.containsAnActor()) {
                continue;
            }
            Actor actor = neighbour.getActor();
            Optional<Location> backStep = new CrowNavigation(actor, neighbour, areaFactory).stepAway(here);
            if (backStep.isPresent()) {
                map.moveActor(actor, backStep.get());
                startled.add(actor + " to " + backStep.get());
            }
        }
        return startled;
    }


    /** @return the name of this state */
    @Override
    public String toString() {
        return "Fleeing";
    }
}
