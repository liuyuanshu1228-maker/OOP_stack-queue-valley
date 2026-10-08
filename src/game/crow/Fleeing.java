package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.creaturestate.Composure;
import game.creaturestate.BaseCreatureState;
import game.creaturestate.CreatureState;
import game.creaturestate.Roosting;
import game.positions.area.Area;
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

    /** The shared instance used by the game; this state holds no per-creature data. */
    public static final Fleeing INSTANCE = new Fleeing();

    /** How many consecutive safe turns it takes for the crow to fully calm down. */
    private static final int CALM_DOWN_TURNS = 3;

    /**
     * Creates this state. The game shares {@link #INSTANCE} because the state is
     * stateless, but another instance (e.g. of a subclass) can be created freely.
     */
    public Fleeing() {
    }

    /**
     * Takes one step toward the perch it last settled on, as long as that step
     * does not bring it closer to the closest actor within sensing range;
     * otherwise (or with no perch) steps directly away from that actor. Stays
     * put if it is cornered or nothing is nearby.
     *
     * @param creature the crow acting
     * @param map the map the crow is on
     * @return a description of what happened
     */
    @Override
    public String act(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        Optional<Location> threat = new CrowSensing(creature, here).nearestActor();
        if (threat.isEmpty()) {
            Optional<Composure> composure = creature.asCapability(Composure.class);
            int safeTurns = composure.isPresent() ? composure.get().getSafeTurns() : 0;
            return creature + " keeps a wary eye out, catching its breath (safe for "
                    + safeTurns + "/" + CALM_DOWN_TURNS + " turns before it calms down).";
        }

        Optional<Location> perch = perchOf(creature);
        Optional<Location> homeward = stepHomeward(creature, here, perch, threat.get());
        if (homeward.isPresent()) {
            map.moveActor(creature, homeward.get());
            return creature + " flees from " + threat.get().getActor() + " at " + threat.get()
                    + " toward its perch at " + perch.get() + ", landing at " + homeward.get() + ".";
        }

        Optional<Location> escape = new CrowNavigation(creature, here).stepAway(threat.get());
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
        CrowSensing sensing = new CrowSensing(creature, map.locationOf(creature));
        boolean safe = sensing.isSafe();
        int safeTurns = updateComposure(creature, safe);

        if (safe && safeTurns >= CALM_DOWN_TURNS) {
            return Perching.INSTANCE;
        }
        if (safe && sensing.foodWithinRange()) {
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
        Optional<Composure> composure = creature.asCapability(Composure.class);
        if (composure.isPresent()) {
            composure.get().resetComposure();
        }
        Location here = map.locationOf(creature);

        List<Item> dropped = ItemDropper.adjacentTo(here).dropAllCarriedBy(creature);
        List<String> startled = startleNeighbours(here, map);

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
    /**
     * Finds the step toward the perch that does not bring the creature closer
     * to the threat.
     *
     * @param creature the crow
     * @param here where the crow stands
     * @param perch the crow's perch, if it has one
     * @param threat the closest actor, who is being fled from
     * @return the Location to step to, or empty if there is no perch or no
     * such step
     */
    private Optional<Location> stepHomeward(Actor creature, Location here,
                                            Optional<Location> perch, Location threat) {
        if (perch.isEmpty()) {
            return Optional.empty();
        }
        return new CrowNavigation(creature, here).stepTowardKeepingClear(perch.get(), threat);
    }

    /**
     * Finds the perch the creature last settled on.
     *
     * @param creature the crow
     * @return the perch, or empty if it has none (or cannot remember one)
     */
    private Optional<Location> perchOf(Actor creature) {
        Optional<Roosting> roosting = creature.asCapability(Roosting.class);
        if (roosting.isEmpty()) {
            return Optional.empty();
        }
        return roosting.get().getPerch();
    }

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
     * there is an enterable tile for them to step back onto.
     *
     * @param here the Location the actors are pushed away from
     * @param map the map the actors are on
     * @return where each actor was pushed, e.g. "Farmer Bob (100/100) to (3, 2) on Inherited Farm"
     */
    private List<String> startleNeighbours(Location here, GameMap map) {
        List<String> startled = new ArrayList<>();
        for (Location neighbour : Area.adjacentTo(here).tiles()) {
            if (!neighbour.containsAnActor()) {
                continue;
            }
            Actor actor = neighbour.getActor();
            Optional<Location> backStep = new CrowNavigation(actor, neighbour).stepAway(here);
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
