package game;

import edu.monash.fit2099.engine.items.Item;

/**
 * A tool that farmers carry to water crops.
 *
 * @see WateringTool
 */
public class WateringCan extends Item implements WateringTool {

    /**
     * Constructor. A watering can is portable, so it can be picked up and
     * dropped like any other item.
     */
    public WateringCan() {
        super("Watering Can", 'w');
        makePortable();
    }
}
