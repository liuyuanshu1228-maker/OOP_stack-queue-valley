package game.positions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A way of travelling to a {@link TravelDestination}: a ladder, a
 * teleportation circle, an alien device, or any mechanism added later.
 * <p>
 * Every method has a default, so a mechanism only overrides the part in which
 * it differs: where the Actor really ends up ({@link #chooseLanding}) and what
 * happens on arrival ({@link #onArrival}).
 */
public interface TravelMethod {

    /**
     * Decides where the Actor really lands. By default this is exactly the
     * intended destination; an unreliable mechanism can override this to land
     * the Actor elsewhere.
     *
     * @param actor the Actor who is travelling
     * @param map the map the Actor is currently on
     * @param intended the Location the Actor chose to travel to
     * @return the Location the Actor lands on
     */
    default Location chooseLanding(Actor actor, GameMap map, Location intended) {
        return intended;
    }

    /**
     * Applies whatever happens once the Actor has arrived. By default nothing
     * happens.
     *
     * @param actor the Actor who has just arrived
     * @param landing the Location the Actor landed on
     * @return a description of what happened, or an empty string if nothing did
     */
    default String onArrival(Actor actor, Location landing) {
        return "";
    }
}
