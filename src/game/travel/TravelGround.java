package game.travel;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TravelMethod;

/**
 * Abstract base class for Ground that transports the Actor standing on it to
 * one of its destinations, such as a ladder or a teleportation circle. It can
 * only be used from the tile itself, never from an adjacent tile. Subclasses
 * only describe how they differ (see {@link TravelMethod}).
 */
public abstract class TravelGround extends Ground implements TravelMethod {

    private final DestinationList destinations = new DestinationList();

    /**
     * Constructor.
     *
     * @param displayChar character to display for this ground
     * @param name the name of this ground
     */
    public TravelGround(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * Adds a destination this ground can transport an Actor to.
     *
     * @param name the name shown to the player in the menu
     * @param location the fixed Location to travel to
     */
    public void addDestination(String name, Location location) {
        destinations.add(name, location);
    }

    /**
     * Offers one travel action per destination, only while the Actor is
     * standing on this ground.
     *
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of this ground from the Actor
     * @return the travel actions if direction is empty, otherwise an empty list
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        if (!direction.isEmpty()) {
            return new ActionList();
        }
        return destinations.actionsFor(this);
    }
}
