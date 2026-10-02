package game.positions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.tools.BreakingTool;

/**
 * Capability interface for anything an Actor can break with a
 * {@link BreakingTool}: a Ground (a rock), an Item (a geode) or anything
 * added later (a treasure chest). Implementing classes define what breaking
 * them does. Checking that the Actor has a tool is done by the calling Action,
 * not by this method.
 */
public interface Breakable {

    /**
     * Breaks this object and applies the effect of breaking it.
     *
     * @param actor the Actor breaking it
     * @param location the Location of the object being broken
     * @return a description of the outcome, to be displayed to the player
     */
    String breakUp(Actor actor, Location location);
}