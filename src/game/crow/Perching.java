package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.CreatureState;
import game.positions.Crop;
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
     * Moves to Foraging once food becomes visible within sensing range.
     * The conditions for moving to Mobbing/Fleeing are added once those
     * states exist.
     *
     * @param creature the crow whose next state is being decided
     * @param map the map the crow is on
     * @return Foraging if food is within sensing range, otherwise this state
     */
    @Override
    public CreatureState nextState(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        boolean foodNearby = LocationUtils.nearestWhere(here, Crow.SENSING_RADIUS,
                candidate -> CrowSensing.hasForageableItems(candidate, creature)).isPresent();

        if (foodNearby) {
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
