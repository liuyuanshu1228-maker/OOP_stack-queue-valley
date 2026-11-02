package game.items.food;

import game.positions.ProcessingFacility;

/**
 * Capability interface for Items that can be processed by a ProcessingFacility.
 * An item says which facilities it can be processed by, the same way
 * {@link game.items.tools.BreakingTool#canBreak} lets a breaking tool say
 * which targets it can break, so an item that can only be processed by some
 * facilities needs no change to {@link game.positions.facilities.Facility}.
 * Raw crops (e.g. Parsnip, Tomato) and all ProcessedProduct layers implement
 * this, allowing a facility to process either a raw product or an
 * already-processed one.
 */
public interface Processable {

    /**
     * Tells whether this item can be processed by the given facility.
     *
     * @param facility the facility that would process this item
     * @return true if this item can be processed by the given facility
     */
    public boolean canBeProcessedBy(ProcessingFacility facility);
}
