package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.creaturestate.CreatureState;
import game.positions.FreelyWaterable;
import game.positions.area.Area;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The crow's default, idle state: wanders aimlessly around the map, watching
 * for food or for actors to mob.
 * <p>
 * From here the crow moves to {@link Mobbing} if exactly one actor is nearby,
 * or to {@link Foraging} once food comes within sensing range; otherwise it
 * stays perching. {@link Mobbing} and {@link Fleeing} both settle back into
 * this state once their own conditions for leaving are met.
 */
public class Perching implements CreatureState {

    /** The shared instance used by the game; this state holds no per-creature data. */
    public static final Perching INSTANCE = new Perching();

    /**
     * Creates this state. The game shares {@link #INSTANCE} because the state is
     * stateless, but another instance (e.g. of a subclass) can be created freely.
     */
    public Perching() {
    }

    /**
     * Moves the crow to a random enterable tile adjacent to its current
     * location, or stays put if there is nowhere to go.
     *
     * @param creature the crow acting
     * @param map the map the crow is on
     * @return a description of what happened
     */
    @Override
    public String act(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        Optional<Location> destination = Area.adjacentTo(here).randomEnterableTile(creature);

        if (destination.isEmpty()) {
            return creature + " perches, with nowhere to go.";
        }

        map.moveActor(creature, destination.get());
        return creature + " hops to a new perch at " + destination.get() + ".";
    }

    /**
     * Moves to Mobbing if exactly one other actor is visible within sensing
     * range and it is carrying something (a lone target worth harassing).
     * Moves to Foraging once food is
     * visible within sensing range. Otherwise stays perching.
     * <p>
     * Mobbing is checked first: if a lone actor is nearby the crow prioritises
     * the confrontation over scavenging for food.
     *
     * @param creature the crow whose next state is being decided
     * @param map the map the crow is on
     * @return the next state
     */
    @Override
    public CreatureState nextState(Actor creature, GameMap map) {
        CrowSensing sensing = new CrowSensing(creature, map.locationOf(creature));

        if (sensing.hasLoneTargetWithItems()) {
            return Mobbing.INSTANCE;
        }

        if (sensing.nearestFood().isPresent()) {
            return Foraging.INSTANCE;
        }

        return this;
    }

    /**
     * The effect of settling back into Perching after being in another
     * state: the crow's droppings count as a free watering for every crop on
     * its own tile and on each tile adjacent to it.
     *
     * @param creature the crow entering this state
     * @param map the map the crow is on
     * @return a description of the effect, listing every crop watered
     */
    @Override
    public String onEnter(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        List<FreelyWaterable> watered = new ArrayList<>();
        for (Location location : Area.centredOn(here).tiles()) {
            FreelyWaterable waterable = location.getGroundAs(FreelyWaterable.class);
            if (waterable != null) {
                waterable.receiveFreeWatering();
                watered.add(waterable);
            }
        }

        if (watered.isEmpty()) {
            return creature + " settles back onto a perch at " + here
                    + ", with no crops within 1 tile to water.";
        }
        return creature + " settles back onto a perch at " + here
                + ", and its droppings count as a free watering for every crop within 1 tile: "
                + watered + ".";
    }

    /** @return the name of this state */
    @Override
    public String toString() {
        return "Perching";
    }
}