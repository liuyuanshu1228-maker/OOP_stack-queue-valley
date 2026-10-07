package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An Action that moves an Actor to a named destination using a
 * {@link TravelMethod}. The method decides where the Actor really lands and
 * what happens on arrival; this Action only moves the Actor and reports it.
 */
public class TravelAction extends Action {

    /** Human-readable destination label shown to the player in the action menu. */
    private final String destinationName;

    /** The intended map location the actor is travelling to. */
    private final Location destinationLocation;

    /** The travel mechanism that determines the actual landing and arrival effect. */
    private final TravelMethod method;

    /**
     * Constructor.
     *
     * @param destinationName the name shown to the player in the menu
     * @param destinationLocation the fixed Location to travel to
     * @param method the mechanism used to travel there
     */
    public TravelAction(String destinationName, Location destinationLocation, TravelMethod method) {
        this.destinationName = destinationName;
        this.destinationLocation = destinationLocation;
        this.method = method;
    }

    /**
     * Moves the Actor to the landing chosen by the travel method, then lets the
     * method apply its arrival effect. If the landing is blocked (for example
     * another Actor is standing there), the Actor stays where they are and
     * nothing else happens.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location landing = method.chooseLanding(actor, map, destinationLocation);
        if (!landing.canActorEnter(actor)) {
            return actor + " cannot travel to " + destinationName
                    + ": the arrival point is blocked.";
        }
        map.moveActor(actor, landing);
        String message = actor + " travels to " + landing + ".";
        if (!landing.equals(destinationLocation)) {
            message += " The journey goes off course, so " + actor + " does not reach "
                    + destinationName + " as intended.";
        }
        String arrivalEffect = method.onArrival(actor, landing);
        return arrivalEffect.isEmpty() ? message : message + " " + arrivalEffect;
    }

    /**
     * Describes what this Action will do if chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " travels to " + destinationName;
    }
}
