package game.items.tools;

import game.positions.Waterable;

/**
 * Capability interface for Items that can be used to water Ground.
 * A tool says which Waterable targets it can water, the same way
 * {@link BreakingTool#canBreak} lets a breaking tool say which targets it can
 * break, so a tool that can only water some targets needs no change to
 * {@link game.actions.WaterAction}.
 */
public interface WateringTool extends HasStaminaCost {

    /**
     * Tells whether this tool can water the given target.
     *
     * @param target the Waterable the carrier wants to water
     * @return true if this tool can water the target
     */
    boolean canWater(Waterable target);
}
