package game.positions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Fire;

/**
 * A teleportation circle. Arriving by one sets the ground alight on every tile
 * around the arrival tile; the arrival tile itself is spared.
 */
public class TeleportationCircle extends TravelGround {

    /** How many turns the fire lit on arrival burns for. */
    private static final int FIRE_LIFETIME_TURNS = 2;
    /** Damage per turn of the burn the fire inflicts. */
    private static final int BURN_DAMAGE_PER_TURN = 5;
    /** How many turns the burn the fire inflicts lasts. */
    private static final int BURN_DURATION_TURNS = 3;

    /**
     * Constructor.
     */
    public TeleportationCircle() {
        super('O', "Teleportation circle");
    }

    /**
     * Sets the tiles around the arrival tile on fire.
     *
     * @param actor the Actor who has just arrived
     * @param landing the Location the Actor landed on
     * @return a description of the fire
     */
    @Override
    public String onArrival(Actor actor, Location landing) {
        LocationUtils.addItemToEachNearby(landing,
                () -> new Fire(FIRE_LIFETIME_TURNS, BURN_DAMAGE_PER_TURN, BURN_DURATION_TURNS));
        return "The ground around " + actor + " bursts into flames!";
    }
}
