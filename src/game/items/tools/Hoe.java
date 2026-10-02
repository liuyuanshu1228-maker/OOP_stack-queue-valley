package game.items.tools;

import edu.monash.fit2099.engine.items.Item;

/**
 * A Hoe that the Farmer can carry and use to till dirt (and potentially other tillable ground) into soil.
 */
public class Hoe extends Item implements TillingTool {

    /** Stamina cost of tilling something with this hoe. */
    private static final int STAMINA_COST = 2;

    /**
     * Constructor.
     */
    public Hoe() {
        super("Hoe", 'h');
        this.makePortable();
    }

    /**
     * @return the stamina cost of tilling with this hoe
     */
    @Override
    public int getStaminaCost() {
        return STAMINA_COST;
    }
}
