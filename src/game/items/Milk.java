package game.items;

import edu.monash.fit2099.engine.items.Item;

/**
 * A bottle of Milk, produced by feeding a Cow.
 * <p>
 * Uses 'K' rather than the more obvious 'M', which is already taken by the
 * Melon crop in this world.
 */
public class Milk extends Item {

    /**
     * Constructor.
     */
    public Milk() {
        super("Milk", 'K');
        this.makePortable();
    }
}
