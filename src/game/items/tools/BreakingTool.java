package game.items.tools;

import game.positions.Breakable;

/**
 * Capability interface for Items that can be used to break {@link Breakable}
 * things. A tool says which targets it can break, so a tool that can break only some
 * targets (for example a hammer that cannot split a geode) needs no change to the
 * BreakAction that asks.
 */
public interface BreakingTool {

    /**
     * Tells whether this tool can break the given target.
     *
     * @param target the thing the carrier wants to break
     * @return true if this tool can break the target
     */
    boolean canBreak(Breakable target);
}
