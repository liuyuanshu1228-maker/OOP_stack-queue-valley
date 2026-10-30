package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.Crop;
import game.crops.Tomato;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

/** Buying a Tomato crop: 500 coins, 50% chance the merchant plants it free. */
public class TomatoPurchase extends CropPurchase {

    /** Coins charged for a Tomato crop. */
    private static final int PRICE = 500;
    /** Chance (out of 100) that the Merchant plants the crop without taking payment. */
    private static final int WAIVE_CHANCE_PERCENT = 50;

    /** The randomness used to roll the payment-waiver chance and handed to the planted Tomato. */
    private final RandomUtils randomUtils;

    /** Builds Areas for the planted Tomato's own abilities. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new TomatoPurchase. Initializes the purchase option with a price of 500 coins.
     *
     * @param randomUtils the randomness used to roll the payment-waiver chance,
     *                    and handed to the planted Tomato
     * @param areaFactory builds the Area searched for a planting tile, and
     *                    handed to the planted Tomato for its own abilities
     */
    public TomatoPurchase(RandomUtils randomUtils, AreaFactory areaFactory) {
        super("Tomato crop", PRICE, areaFactory);
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * Rolls a 50% chance for the Merchant to waive payment and plant the Tomato crop for free.
     *
     * @return {@code true} if payment is waived, {@code false} otherwise
     */
    @Override
    protected boolean rollPaymentWaiver() {
        return randomUtils.rollPercent(WAIVE_CHANCE_PERCENT);
    }

    /**
     * Creates a new Tomato crop ground to be planted.
     *
     * @return a new {@link Tomato} crop instance
     */
    @Override
    protected Crop createCrop() {
        return new Tomato(randomUtils, areaFactory);
    }

    /**
     * Returns the base purchase message after the Tomato crop is planted.
     *
     * @param buyer the Actor buying the crop
     * @param merchant the Merchant selling the crop
     * @param cropLocation the Location where the Tomato crop was planted
     * @return a description of the purchase outcome
     */
    @Override
    protected String applyPlantingEffect(Actor buyer, Actor merchant, Location cropLocation) {
        return buyer + " buys a Tomato crop from " + merchant + ", planted at " + cropLocation + ".";
    }
}
