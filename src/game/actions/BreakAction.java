package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.tools.BreakingTool;
import game.positions.Breakable;

/**
 * An Action that allows an Actor to break a {@link Breakable}, provided they
 * are carrying a {@link BreakingTool}. What breaking actually does is up to the
 * Breakable.
 */
public class BreakAction extends Action {

    private final Breakable target;
    private final Location location;
    private final String whereItIs;

    /**
     * Constructor.
     *
     * @param target the thing to be broken
     * @param location the Location of the thing to be broken
     * @param whereItIs where the target is relative to the Actor, used to tell
     *                  apart identical targets in the menu (e.g. "North", "in inventory")
     */
    public BreakAction(Breakable target, Location location, String whereItIs) {
        this.target = target;
        this.location = location;
        this.whereItIs = whereItIs;
    }

    /**
     * Performs the breaking attempt. If the Actor is carrying a breaking tool,
     * delegates to the target; otherwise fails without any side effects.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        boolean hasTool = !actor.getInventory().getItemsAs(BreakingTool.class).isEmpty();
        if (!hasTool) {
            return actor + " needs a breaking tool to break the " + target + ".";
        }
        return target.breakUp(actor, location);
    }

    /**
     * Describes what this Action will do if chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " breaks the " + target + " (" + whereItIs + ")";
    }
}
