package game.items.tools;

import edu.monash.fit2099.engine.items.Item;
import game.positions.Breakable;

/**
 * A pickaxe that the Farmer can carry and use to break rocks, copper nodes and
 * geodes.
 */
public class Pickaxe extends Item implements BreakingTool {

    /**
     * Creates a new Pickaxe.
     */
    public Pickaxe() {
        super("Pickaxe", 'x');
        this.makePortable();
    }

    /**
     * A pickaxe can break every Breakable thing: rocks, copper nodes and geodes.
     *
     * @param target the thing the carrier wants to break
     * @return always true
     */
    @Override
    public boolean canBreak(Breakable target) {
        return true;
    }
}
