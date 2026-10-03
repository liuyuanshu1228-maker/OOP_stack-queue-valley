package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.CreatureState;
import game.crops.Crop;
import game.positions.LocationUtils;

import java.util.Optional;

/**
 * The crow's default state: wanders aimlessly around the map, watching for
 * food or for actors to mob.
 * <p>
 * This is the only state wired up so far. The conditions that would move
 * the crow into Foraging, Mobbing or Fleeing are added alongside those
 * states, in {@link #nextState}.
 */
public final class Perching implements CreatureState {

    /** The single shared instance of this stateless state. */
    public static final Perching INSTANCE = new Perching();

    private Perching() {
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
        Optional<Location> destination = LocationUtils.randomEnterableNearby(here, creature);

        if (destination.isEmpty()) {
            return creature + " perches, with nowhere to go.";
        }

        map.moveActor(creature, destination.get());
        return creature + " hops to a new perch.";
    }

    /**
     * Moves to Mobbing if exactly one other actor is visible within sensing
     * range (a lone target worth harassing). Moves to Foraging once food is
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
        Location here = map.locationOf(creature);

        if (CrowSensing.countActorsWithinRange(here) == 1) {
            return Mobbing.INSTANCE;
        }

        if (CrowSensing.nearestFood(here, creature).isPresent()) {
            return Foraging.INSTANCE;
        }

        return this;
    }

    /**
     * The effect of settling back into Perching after being in another
     * state: if the crow happens to be standing over a crop, its droppings
     * count as a free watering for it.
     *
     * @param creature the crow entering this state
     * @param map the map the crow is on
     * @return a description of the effect
     */
    @Override
    public String onEnter(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        Crop crop = here.getGroundAs(Crop.class);

        if (crop == null) {
            return creature + " settles back onto a perch.";
        }

        crop.recordWatering();
        return creature + " settles onto the " + crop + ", and its droppings count as a free watering.";
    }

    @Override
    public String toString() {
        return "Perching";
    }
}
