package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.MerchantContext;
import game.actions.Purchasable;
import game.crops.Crop;
import game.positions.Plantable;
import game.positions.area.AreaFactory;
import game.positions.area.PlantableTile;
import game.statistics.FarmerStatistics;

import java.util.Optional;

/**
 * Abstract base class for purchasing crops from a Merchant.
 * Encapsulates common attributes and planting logic on a {@link Plantable}
 * tile (such as Soil) adjacent to the Merchant's location.
 */
public abstract class CropPurchase implements Purchasable {

    /** The name of the crop on sale, shown in the Merchant's menu. */
    private final String productName;
    /** The number of coins the crop costs. */
    private final int price;

    /** Builds the Area searched for a tile to plant the crop on. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new CropPurchase.
     *
     * @param productName the name of the crop being purchased
     * @param price the cost of the crop in coins
     * @param areaFactory builds the Area searched for a tile to plant the crop on
     */
    protected CropPurchase(String productName, int price, AreaFactory areaFactory) {
        this.productName = productName;
        this.price = price;
        this.areaFactory = areaFactory;
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
     * Executes the crop purchase: finds a random adjacent Plantable tile near the
     * Merchant to plant the crop (returning early if none exists), then charges
     * the buyer (unless waived) and triggers any crop-specific planting effect.
     *
     * @param buyer the Actor buying this crop
     * @param merchantContext the Merchant selling it and its current Location
     * @return a description of the purchase outcome
     */
    @Override
    public final String purchase(Actor buyer, MerchantContext merchantContext) {
        Actor merchant = merchantContext.getMerchant();
        Optional<Location> plantingSpot =
                areaFactory.adjacentTo(merchantContext.getLocation()).filter(new PlantableTile()).randomTile();

        if (plantingSpot.isEmpty()) {
            return buyer + " tries to buy a " + productName
                    + ", but " + merchant + " has no room to plant it nearby.";
        }

        Location plantedLocation = plantingSpot.get();
        plantedLocation.getGroundAs(Plantable.class).plant(createCrop(), plantedLocation);

        boolean waived = chargeUnlessWaived(buyer);
        String message = applyPlantingEffect(buyer, merchant, plantedLocation);
        return waived
                ? message + " " + merchant + " plants it for free, out of generosity."
                : message;
    }

    /**
     * Takes the price from the buyer, unless this purchase is waived.
     *
     * @param buyer the Actor buying this crop
     * @return true if the payment was waived and nothing was taken
     */
    private boolean chargeUnlessWaived(Actor buyer) {
        boolean waived = rollPaymentWaiver();
        if (!waived) {
            buyer.modifyStatistic(FarmerStatistics.COINS, StatisticOperations.DECREASE, price);
        }
        return waived;
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
     * @param buyer the Actor buying the crop
     * @param merchant the Merchant selling the crop
     * @param cropLocation the Location where the new crop was planted
     * @return a description of the purchase outcome
     */
    protected abstract String applyPlantingEffect(Actor buyer, Actor merchant,
                                                  Location cropLocation);
}
