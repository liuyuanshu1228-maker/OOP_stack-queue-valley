package game.travel;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TravelMethod;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

import java.util.Optional;

/**
 * An unreliable alien device. While it is carried it offers a choice of
 * destinations, wherever the carrier happens to be, but each use has a chance
 * of malfunctioning and dropping the carrier at a random place on the map they
 * are standing on instead.
 */
public class AlienDevice extends Item implements TravelMethod {

    /** Chance (out of 100) that a use malfunctions. */
    private static final int MALFUNCTION_CHANCE_PERCENT = 50;

    private final DestinationList destinations = new DestinationList();

    /** The randomness used to roll the malfunction chance. */
    private final RandomUtils randomUtils;

    /** Builds the Area of the whole map, searched for a random landing tile on a malfunction. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new AlienDevice.
     *
     * @param randomUtils the randomness used to roll the malfunction chance
     * @param areaFactory builds the Area of the whole map, searched for a
     *                    random landing tile on a malfunction
     */
    public AlienDevice(RandomUtils randomUtils, AreaFactory areaFactory) {
        super("Alien device", 'a');
        this.makePortable();
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * Adds a destination this device can transport its carrier to.
     *
     * @param name the name shown to the player in the menu
     * @param location the fixed Location to travel to
     */
    public void addDestination(String name, Location location) {
        destinations.add(name, location);
    }

    /**
     * Offers one travel action per destination to whoever is carrying the device.
     *
     * @param owner the Actor carrying this device
     * @param map the map the owner is on
     * @return the travel actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        return destinations.actionsFor(this);
    }

    /**
     * Rolls the chance of a malfunction. On a malfunction the Actor is sent to
     * a random tile of the map they are currently on instead of the intended
     * destination.
     *
     * @param actor the Actor who is travelling
     * @param map the map the Actor is currently on
     * @param intended the Location the Actor chose to travel to
     * @return the intended Location, or a random enterable Location on the current map
     */
    @Override
    public Location chooseLanding(Actor actor, GameMap map, Location intended) {
        if (randomUtils.rollPercent(MALFUNCTION_CHANCE_PERCENT)) {
            Optional<Location> landing = areaFactory.wholeMap(map).randomEnterableTile(actor);
            if (landing.isPresent()) {
                return landing.get();
            }
        }
        return intended;
    }
}
