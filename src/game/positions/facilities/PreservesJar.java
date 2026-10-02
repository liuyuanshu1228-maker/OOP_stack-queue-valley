package game.positions.facilities;

import edu.monash.fit2099.engine.items.Item;
import game.items.food.PreservedProduct;

/**
 * A Preserves Jar facility. Processing a product with it adds one Preserved layer.
 */
public class PreservesJar extends Facility {

    /**
     * Constructor.
     */
    public PreservesJar() {
        super('J', "Preserves Jar");
    }

    /**
     * Wraps the given product in a new Preserved layer.
     *
     * @param product the Item to be processed (raw or already processed)
     * @return a new PreservedProduct wrapping the given product
     */
    @Override
    public Item process(Item product) {
        return new PreservedProduct(product);
    }
}
