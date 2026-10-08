package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.area.Area;
import game.positions.area.EnterableBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        return onlyIfMoving(options().nearestTo(target));
    }

    /**
     * Picks the adjacent tile (that the actor could step onto) which brings
     * the actor closest to the target, without bringing it any closer to the
     * threat than it already is.
     *
     * @param target the Location being approached
     * @param threat the Location that must not be approached
     * @return the best adjacent Location to step to, or empty if no such tile
     * gets any closer to the target than staying put
     */
    Optional<Location> stepTowardKeepingClear(Location target, Location threat) {
        return onlyIfMoving(options().noCloserTo(threat, from).nearestTo(target));
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
        return onlyIfMoving(options().farthestFrom(threat));
    }

    /**
     * The tiles the actor could end its turn on: the tile it stands on, listed
     * first, followed by the adjacent tiles it is able to enter. Staying put is
     * listed first and the first best option wins, so a step has to be
     * strictly better than staying to be chosen.
     *
     * @return the options for a step, starting with staying put
     */
    private Area options() {
        List<Location> options = new ArrayList<>();
        options.add(from);
        options.addAll(Area.adjacentTo(from).filter(new EnterableBy(actor)).tiles());
        return new Area(options);
    }

    /**
     * Turns the best option into a step: staying put is not a step.
     *
     * @param best the best option, if any
     * @return the best option if it is a tile other than the one the actor
     * stands on, otherwise empty
     */
    private Optional<Location> onlyIfMoving(Optional<Location> best) {
        if (best.isPresent() && !best.get().equals(from)) {
            return best;
        }
        return Optional.empty();
    }
}
