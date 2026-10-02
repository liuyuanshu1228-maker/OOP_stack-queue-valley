package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.ParsnipItem;
import game.crops.Crop;
import game.positions.LocationUtils;
import game.crops.Parsnip;

/** 
 * Buying a Parsnip crop: 250 coins, drops 3 Parsnips once planted. 
 * 
 */
public class ParsnipPurchase extends CropPurchase {

    private static final int PRICE = 250;
    private static final int PARSNIPS_PRODUCED = 3;

    /**
     * Constructor for ParsnipPurchase. Initializes the purchase option with a price of 250 coins.
     */
    public ParsnipPurchase() {
        super("Parsnip crop", PRICE);
    }

    /**
     * Creates a new Parsnip crop ground to be planted.
     *
     * @return a new {@link Parsnip} crop instance
     */
    @Override
    protected Crop createCrop() {
        return new Parsnip();
    }

    /**
     * Drops 3 {@link ParsnipItem} instances on randomly chosen tiles adjacent to the planted crop.
     *
     * @param buyer        the Actor buying the crop
     * @param merchant     the Merchant selling the crop
     * @param cropLocation the Location where the Parsnip crop was planted
     * @return a description of the purchase outcome
     */
    @Override
    protected String applyPlantingEffect(Actor buyer, Actor merchant, Location cropLocation) {
        for (int i = 0; i < PARSNIPS_PRODUCED; i++) {
            LocationUtils.dropItemNearby(cropLocation, new ParsnipItem(), random);
        }
        return buyer + " buys a Parsnip crop from " + merchant + ".";
    }
}