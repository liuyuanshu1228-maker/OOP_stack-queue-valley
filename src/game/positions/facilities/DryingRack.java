package game.positions.facilities;

import edu.monash.fit2099.engine.items.Item;
import game.items.food.DriedProduct;

/**
 * A Drying Rack facility. Processing a product with it adds one Dried layer.
 */
public class DryingRack extends Facility {

    /**
     * Constructor.
     */
    public DryingRack() {
        super('R', "Drying Rack");
    }

    /**
     * Wraps the given product in a new Dried layer.
     *
     * @param product the Item to be processed (raw or already processed)
     * @return a new DriedProduct wrapping the given product
     */
    @Override
    public Item process(Item product) {
        return new DriedProduct(product);
    }
}
