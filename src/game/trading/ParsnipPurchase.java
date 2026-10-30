package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.ParsnipItem;
import game.crops.Crop;
import game.positions.area.AreaFactory;
import game.positions.area.ItemDropper;
import game.crops.Parsnip;
import game.random.RandomUtils;

/**
 * Buying a Parsnip crop: 250 coins, drops 3 Parsnips once planted.
 */
public class ParsnipPurchase extends CropPurchase {

    /** Coins charged for a Parsnip crop. */
    private static final int PRICE = 250;
    /** Number of Parsnips dropped next to the crop when it is bought. */
    private static final int PARSNIPS_PRODUCED = 3;

    /** The randomness handed to the planted Parsnip. */
    private final RandomUtils randomUtils;

    /** Builds the Area the bonus Parsnips are dropped onto, and handed to the planted Parsnip. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new ParsnipPurchase. Initializes the purchase option with a price of 250 coins.
     *
     * @param randomUtils the randomness handed to the planted Parsnip
     * @param areaFactory builds the Area searched for a planting tile and the
     *                    Area the bonus Parsnips are dropped onto, and handed
     *                    to the planted Parsnip for its own abilities
     */
    public ParsnipPurchase(RandomUtils randomUtils, AreaFactory areaFactory) {
        super("Parsnip crop", PRICE, areaFactory);
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * Creates a new Parsnip crop ground to be planted.
     *
     * @return a new {@link Parsnip} crop instance
     */
    @Override
    protected Crop createCrop() {
        return new Parsnip(randomUtils, areaFactory);
    }

    /**
     * Drops 3 {@link ParsnipItem} instances on randomly chosen tiles adjacent to the planted crop.
     *
     * @param buyer the Actor buying the crop
     * @param merchant the Merchant selling the crop
     * @param cropLocation the Location where the Parsnip crop was planted
     * @return a description of the purchase outcome
     */
    @Override
    protected String applyPlantingEffect(Actor buyer, Actor merchant, Location cropLocation) {
        ItemDropper dropper = new ItemDropper(areaFactory.adjacentTo(cropLocation));
        for (int i = 0; i < PARSNIPS_PRODUCED; i++) {
            dropper.dropOnRandomTile(new ParsnipItem());
        }
        return buyer + " buys a Parsnip crop from " + merchant + ", planted at " + cropLocation + ".";
    }
}
