package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.area.Area;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * The steps an actor can take from where it stands: one tile toward, or away
 * from, a reference Location.
 * <p>
 * One instance describes one actor at one Location. It is used by the crow's
 * states ({@link Foraging}, {@link Mobbing}, {@link Fleeing}), including to
 * push other actors back when the crow startles them.
 */
class CrowNavigation {

    /** The actor that would be doing the stepping. */
    private final Actor actor;

    /** The Location the actor would step from. */
    private final Location from;

    /**
     * Creates the navigation of an actor at a Location.
     *
     * @param actor the Actor that would be doing the stepping
     * @param from the Location to step from
     */
    CrowNavigation(Actor actor, Location from) {
        this.actor = actor;
        this.from = from;
    }

    /**
     * Picks the adjacent tile (that the actor could step onto) which brings
     * the actor closest to the target.
     *
     * @param target the Location being approached
     * @return the best adjacent Location to step to, or empty if no adjacent
     * tile is enterable or gets any closer than staying put
     */
    Optional<Location> stepToward(Location target) {
        return improvingStep(options -> options.nearestTo(target));
    }

    /**
     * Picks the adjacent tile (that the actor could step onto) which takes
     * the actor furthest away from the threat.
     *
     * @param threat the Location being fled from
     * @return the best adjacent Location to step to, or empty if no adjacent
     * tile is enterable or gets any further away than staying put
     */
    Optional<Location> stepAway(Location threat) {
        return improvingStep(options -> options.farthestFrom(threat));
    }

    /**
     * Chooses a step from the enterable adjacent tiles, but only if it beats
     * staying put. Staying put is listed first among the options, and the
     * first best option wins, so a step has to be strictly better than
     * staying to be chosen.
     *
     * @param choose how to pick the best option out of an area
     * @return the chosen adjacent Location, or empty if staying put is best
     */
    private Optional<Location> improvingStep(Function<Area, Optional<Location>> choose) {
        List<Location> options = new ArrayList<>();
        options.add(from);
        options.addAll(Area.adjacentTo(from).filter(tile -> tile.canActorEnter(actor)).tiles());
        return choose.apply(new Area(options)).filter(best -> !best.equals(from));
    }
}
