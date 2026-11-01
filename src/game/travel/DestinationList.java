package game.travel;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TravelAction;
import game.actions.TravelMethod;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The destinations reachable through one travel mechanism. It is shared by
 * every mechanism (a Ground such as a ladder, an Item such as the alien device)
 * so that none of them repeats the bookkeeping, and so that any mechanism can
 * lead to several destinations.
 */
public class DestinationList {

    /** Each destination by its menu name, in the order it was added. */
    private final Map<String, Location> destinations = new LinkedHashMap<>();

    /**
     * Creates an empty list of destinations.
     */
    public DestinationList() {
    }

    /**
     * Adds a destination.
     *
     * @param name the name shown to the player in the menu
     * @param location the fixed Location to travel to
     */
    public void add(String name, Location location) {
        destinations.put(name, location);
    }

    /**
     * Creates one TravelAction per destination.
     *
     * @param method the mechanism that would be used to travel
     * @return the actions offering every destination
     */
    public ActionList actionsFor(TravelMethod method) {
        ActionList actions = new ActionList();
        for (Map.Entry<String, Location> entry : destinations.entrySet()) {
            actions.add(new TravelAction(entry.getKey(), entry.getValue(), method));
        }
        return actions;
    }
}
