package game.items.tools;

import game.positions.Tillable;

/**
 * Capability interface for Items that can be used to till Ground.
 * A tool says which Tillable targets it can till, the same way
 * {@link BreakingTool#canBreak} lets a breaking tool say which targets it can
 * break, so a tool that can only till some targets needs no change to
 * {@link game.actions.TillAction}.
 */
public interface TillingTool extends HasStaminaCost {

    /**
     * Tells whether this tool can till the given target.
     *
     * @param target the Tillable the carrier wants to till
     * @return true if this tool can till the target
     */
    boolean canTill(Tillable target);
}
