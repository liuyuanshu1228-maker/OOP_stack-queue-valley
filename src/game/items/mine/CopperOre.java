package game.items.mine;

import edu.monash.fit2099.engine.items.Item;

/**
 * Copper ore, dropped when a {@link game.positions.mine.CopperNode} is broken. It has no use until
 * a requirement that uses copper ore is implemented.
 */
public class CopperOre extends Item {

    /**
     * Creates a new CopperOre.
     */
    public CopperOre() {
        super("Copper ore", 'u');
        this.makePortable();
    }
}
