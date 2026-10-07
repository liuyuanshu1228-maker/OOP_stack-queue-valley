package game.items.tools;

import edu.monash.fit2099.engine.items.Item;

/**
 * A Watering Can that the Farmer can carry and use to water dirt (and potentially other
 * waterable ground) into a puddle. Has an unlimited water supply.
 */
public class WateringCan extends Item implements WateringTool {

    /** Stamina cost of watering something with this can. */
    private static final int STAMINA_COST = 2;

    /**
     * Constructor.
     */
    public WateringCan() {
        super("Watering Can", 'w');
        this.makePortable();
    }

    /**
     * @return the stamina cost of watering with this can
     */
    @Override
    public int getStaminaCost() {
        return STAMINA_COST;
    }
}
