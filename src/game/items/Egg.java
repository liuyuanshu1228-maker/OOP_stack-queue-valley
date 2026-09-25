package game.items;

import edu.monash.fit2099.engine.items.Item;

/**
 * An Egg, produced by feeding a Chicken.
 */
public class Egg extends Item {

    /**
     * Constructor.
     */
    public Egg() {
        super("Egg", '0');
        this.makePortable();
    }
}
