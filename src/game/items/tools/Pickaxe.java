package game.items.tools;

import edu.monash.fit2099.engine.items.Item;

/**
 * A pickaxe that the Farmer can carry and use to break rocks, copper nodes and
 * geodes.
 */
public class Pickaxe extends Item implements BreakingTool {

    /**
     * Constructor.
     */
    public Pickaxe() {
        super("Pickaxe", 'p');
        this.makePortable();
    }
}
