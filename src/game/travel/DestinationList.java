package game.travel;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.positions.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * The destinations reachable through one travel mechanism. It is shared by
 * every mechanism (a Ground such as a ladder, an Item such as the alien device)
 * so that none of them repeats the bookkeeping, and so that any mechanism can
 * lead to several destinations.
 */
class DestinationList {

    private final List<TravelDestination> destinations = new ArrayList<>();

    /**
     * Adds a destination.
     *
     * @param name the name shown to the player in the menu
     * @param location the fixed Location to travel to
     */
    void add(String name, Location location) {
        destinations.add(new TravelDestination(name, location));
    }

    /**
     * Creates one TravelAction per destination.
     *
     * @param method the mechanism that would be used to travel
     * @return the actions offering every destination
     */
    ActionList actionsFor(TravelMethod method) {
        ActionList actions = new ActionList();
        for (TravelDestination destination : destinations) {
            actions.add(new TravelAction(destination, method));
        }
        return actions;
    }
}
