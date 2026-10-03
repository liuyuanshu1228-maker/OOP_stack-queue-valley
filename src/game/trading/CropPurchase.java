package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.crops.Crop;
import game.statistics.FarmerStatistics;
import game.util.RandomUtils;

/**
 * Abstract base class for purchasing crops from a Merchant.
 * Encapsulates common attributes and planting logic on a non-crop tile
 * adjacent to the Merchant's location.
 */
public abstract class CropPurchase implements Purchasable {

    private final String productName;
    private final int price;

    /**
     * Constructor for CropPurchase.
     *
     * @param productName the name of the crop being purchased
     * @param price       the cost of the crop in coins
     */
    protected CropPurchase(String productName, int price) {
        this.productName = productName;
        this.price = price;
    }

    /**
     * Returns the display name of this purchasable crop.
     *
     * @return the crop product name
     */
    @Override
    public final String getProductName() {
        return productName;
    }

    /**
     * Returns the purchase price of this crop in coins.
     *
     * @return the price in coins
     */
    @Override
    public final int getPrice() {
        return price;
    }

    /**
     * Executes the crop purchase: finds a random adjacent non-crop tile near the
     * Merchant to plant the crop (returning early if none exists), then charges
     * the buyer (unless waived) and triggers any crop-specific planting effect.
     *
     * @param buyer            the Actor buying this crop
     * @param merchant         the Merchant selling it
     * @param merchantLocation the Location of the Merchant
     * @return a description of the purchase outcome
     */
    @Override
    public final String purchase(Actor buyer, Actor merchant, Location merchantLocation) {
        var candidates = merchantLocation.getNearbyLocations(1).stream()
                .filter(adjacent -> adjacent.getGroundAs(Crop.class) == null)
                .toList();

        if (candidates.isEmpty()) {
            return buyer + " tries to buy a " + productName
                    + ", but " + merchant + " has no room to plant it nearby.";
        }

        Location plantedLocation = RandomUtils.pickRandom(candidates);
        plantedLocation.setGround(createCrop());

        boolean waived = rollPaymentWaiver();
        if (!waived) {
            buyer.modifyStatistic(FarmerStatistics.COINS, StatisticOperations.DECREASE, price);
        }

        String message = applyPlantingEffect(buyer, merchant, plantedLocation);
        return waived ? message + " " + merchant + " plants it for free, out of generosity." : message;
    }

    /**
     * Determines whether the payment for this crop purchase is waived.
     * Defaults to {@code false}; subclasses may override to introduce a waiver chance.
     *
     * @return {@code true} if the crop is planted for free, {@code false} otherwise
     */
    protected boolean rollPaymentWaiver() {
        return false;
    }

    /**
     * Factory method that creates a new instance of the specific Crop ground to plant.
     *
     * @return a new Crop instance
     */
    protected abstract Crop createCrop();

    /**
     * Applies any additional side effect after the crop is planted and returns the base
     * purchase outcome message.
     *
     * @param buyer        the Actor buying the crop
     * @param merchant     the Merchant selling the crop
     * @param cropLocation the Location where the new crop was planted
     * @return a description of the purchase outcome
     */
    protected abstract String applyPlantingEffect(Actor buyer, Actor merchant, Location cropLocation);
}