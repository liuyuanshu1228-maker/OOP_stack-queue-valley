package game.items;

import edu.monash.fit2099.engine.items.Item;

/**
 * A Watering Can that the Farmer can carry and use to water dirt (and potentially other waterable ground) into a puddle.
 * Has an unlimited water supply.
 */
public class WateringCan extends Item implements WateringTool {

    /**
     * Constructor.
     */
    public WateringCan() {
        super("Watering Can", 'w');
        this.makePortable();
    }
}
