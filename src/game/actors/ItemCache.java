package game.actors;

import edu.monash.fit2099.engine.items.Item;

/**
 * Capability interface for an Actor that remembers Items it has
 * deliberately hidden away, so it doesn't treat its own stash as something
 * new to seek out.
 */
public interface ItemCache {

    /**
     * @param item the Item to check
     * @return true if this Actor remembers hiding this Item
     */
    boolean remembers(Item item);

    /**
     * Records that this Actor has hidden the given Item.
     *
     * @param item the Item that was hidden
     */
    void remember(Item item);
}
