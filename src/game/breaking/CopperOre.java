package game.breaking;

import edu.monash.fit2099.engine.items.Item;

/**
 * Copper ore, dropped when a {@link CopperNode} is broken. It has no use until
 * a requirement that uses copper ore is implemented.
 */
public class CopperOre extends Item {

    /**
     * Constructor.
     */
    public CopperOre() {
        super("Copper ore", 'c');
        this.makePortable();
    }
}
