package game.items;

import edu.monash.fit2099.engine.items.Item;
import game.positions.Rock;

/**
 * A stone, dropped when a {@link Rock} is broken. It has no use until a
 * requirement that uses stones is implemented.
 */
public class Stone extends Item {

    /**
     * Constructor.
     */
    public Stone() {
        super("Stone", 's');
        this.makePortable();
    }
}
