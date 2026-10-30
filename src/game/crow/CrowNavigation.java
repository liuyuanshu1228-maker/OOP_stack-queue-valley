package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.area.Area;
import game.positions.area.AreaFactory;
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
 * push other actors back when the crow startles them. Because the actor it
 * navigates is not always the crow itself (a startled neighbour can be any
 * kind of Actor), the AreaFactory it needs is passed in explicitly by the
 * caller, rather than queried from the actor's own capabilities.
 */
public class CrowNavigation {

    /** The actor that would be doing the stepping. */
    private final Actor actor;

    /** The Location the actor would step from. */
    private final Location from;

    /** Builds the Area of tiles the actor could step onto. */
    private final AreaFactory areaFactory;

    /**
     * Creates the navigation of an actor at a Location.
     *
     * @param actor the Actor that would be doing the stepping
     * @param from the Location to step from
     * @param areaFactory builds the Area of tiles the actor could step onto
     */
    public CrowNavigation(Actor actor, Location from, AreaFactory areaFactory) {
        this.actor = actor;
        this.from = from;
        this.areaFactory = areaFactory;
    }

    /**
     * Picks the adjacent tile (that the actor could step onto) which brings
     * the actor closest to the target.
     *
     * @param target the Location being approached
     * @return the best adjacent Location to step to, or empty if no adjacent
     * tile is enterable or gets any closer than staying put
     */
    public Optional<Location> stepToward(Location target) {
        return onlyIfMoving(options().nearestTo(target));
    }

    /**
     * Picks the adjacent tile (that the actor could step onto) which takes
     * the actor furthest away from the threat.
     *
     * @param threat the Location being fled from
     * @return the best adjacent Location to step to, or empty if no adjacent
     * tile is enterable or gets any further away than staying put
     */
    public Optional<Location> stepAway(Location threat) {
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
        options.addAll(areaFactory.adjacentTo(from).filter(new EnterableBy(actor)).tiles());
        return areaFactory.of(options);
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
