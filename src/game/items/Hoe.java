package game.items;

import edu.monash.fit2099.engine.items.Item;

/**
 * A Hoe that the Farmer can carry and use to till dirt (and potentially other tillable ground) into soil.
 */
public class Hoe extends Item implements TillingTool {

    /**
     * Constructor.
     */
    public Hoe() {
        super("Hoe", 'h');
        this.makePortable();
    }
}
