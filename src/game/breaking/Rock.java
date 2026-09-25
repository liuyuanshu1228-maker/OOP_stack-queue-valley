package game.breaking;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.Chance;
import game.LocationUtils;
import java.util.Optional;

/**
 * A rock. It cannot be entered, so it has to be broken to clear the way.
 * Breaking it drops a stone and may release a shockwave that throws the
 * breaker onto a random tile next to the rock.
 */
public class Rock extends BreakableGround {

    /** Chance (out of 100) that breaking a rock releases a shockwave. */
    private static final int SHOCKWAVE_CHANCE_PERCENT = 50;

    /**
     * Constructor.
     */
    public Rock() {
        super('#', "Rock");
    }

    /**
     * @return a new Stone
     */
    @Override
    Item createDrop() {
        return new Stone();
    }

    /**
     * Rolls the chance of a shockwave and, if there is one, throws the Actor
     * onto a random enterable tile next to the rock.
     *
     * @param actor the Actor who broke the rock
     * @param location the Location the rock occupied
     * @return a description of what happened
     */
    @Override
    String afterBreaking(Actor actor, Location location) {
        if (!Chance.rollPercent(SHOCKWAVE_CHANCE_PERCENT)) {
            return "The rock crumbles quietly.";
        }
        Optional<Location> landing = LocationUtils.randomEnterableNearby(location, actor);
        if (landing.isEmpty()) {
            return "A shockwave ripples out, but there is nowhere to throw " + actor + ".";
        }
        location.map().moveActor(actor, landing.get());
        return "A shockwave throws " + actor + " to " + landing.get() + ".";
    }
}
