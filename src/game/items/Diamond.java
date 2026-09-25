package game.items;

import edu.monash.fit2099.engine.items.Item;

/**
 * A diamond, produced when a {@link Geode} splits open. It has no use until a
 * requirement that uses diamonds is implemented.
 */
public class Diamond extends Item {

    /**
     * Constructor.
     */
    public Diamond() {
        super("Diamond", 'd');
        this.makePortable();
    }
}
