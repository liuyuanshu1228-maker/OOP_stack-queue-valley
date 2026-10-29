package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.tools.BreakingTool;
import game.items.tools.EnchantmentTrigger;
import game.positions.Breakable;

import java.util.Optional;

/**
 * An Action that allows an Actor to break a {@link Breakable}, provided they
 * are carrying a {@link BreakingTool}. What breaking actually does is up to the
 * Breakable. Once the Breakable has been broken, the specific tool used is
 * also given a chance to fire any Enchantment attached to it (see
 * {@link EnchantmentTrigger}).
 */
public class BreakAction extends Action {

    /** The Breakable object (ground tile or item) to be broken. */
    private final Breakable target;

    /** The map location of the target being broken. */
    private final Location location;

    /**
     * Context string (e.g. "in inventory", "North") used to distinguish identical targets
     * in the menu.
     */
    private final String whereItIs;

    /**
     * Creates a new BreakAction.
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
     * Performs the breaking attempt. If the Actor is carrying a tool that can break
     * the target, delegates to the target, then gives that tool a chance to fire
     * any Enchantment attached to it; otherwise fails without any side effects.
     *
     * @param actor the actor performing the action
     * @param map the map the actor is on
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Optional<BreakingTool> tool = toolForTarget(actor);
        if (tool.isEmpty()) {
            return actor + " needs a breaking tool to break the " + target + ".";
        }
        String breakReport = target.breakUp(actor, location);
        return breakReport + new EnchantmentTrigger().fire(actor, tool.get(), location);
    }

    /**
     * Finds a tool the Actor carries that can break the target.
     *
     * @param actor the actor who wants to break the target
     * @return the first tool in the actor's inventory that can break the target, if any
     */
    private Optional<BreakingTool> toolForTarget(Actor actor) {
        for (BreakingTool tool : actor.getInventory().getItemsAs(BreakingTool.class)) {
            if (tool.canBreak(target)) {
                return Optional.of(tool);
            }
        }
        return Optional.empty();
    }

    /**
     * Describes what this Action will do if chosen in the menu.
     *
     * @param actor the actor performing the action
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " breaks the " + target + " (" + whereItIs + ")";
    }
}
