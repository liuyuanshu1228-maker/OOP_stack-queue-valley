package game.ground;

import edu.monash.fit2099.engine.items.Item;

/**
 * Capability interface for Ground that can process a portable, Processable Item
 * into a new processed product. Implementing classes (facilities) define which
 * kind of processing layer is produced, without knowing the concrete type of
 * the Item being processed.
 */
public interface ProcessingFacility {

    /**
     * Wraps the given product in a new processing layer.
     *
     * @param product the Item to be processed (raw or already processed)
     * @return a new Item representing the product with one additional processing layer
     */
    Item process(Item product);
}
