package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.items.Sword;
import game.statistics.FarmerStatistics;

import java.util.Random;

/** Buying a Sword: 3000 coins, 50% chance the merchant gives nothing. */
public class SwordPurchase implements Purchasable {

    private static final int PRICE = 3000;
    private static final int FAIL_CHANCE_PERCENT = 50;

    private final Random random = new Random();

    /**
     * Returns the display name of the Sword product.
     *
     * @return the string "Sword"
     */
    @Override
    public String getProductName() {
        return "Sword";
    }

    /**
     * Returns the purchase price of the Sword in coins.
     *
     * @return the price in coins (3000)
     */
    @Override
    public int getPrice() {
        return PRICE;
    }

    /**
     * Deducts the Sword price from the buyer and either adds a new {@link Sword}
     * to the buyer's inventory or results in the Merchant pocketing the coins (50% chance).
     *
     * @param buyer            the Actor buying this product
     * @param merchant         the Merchant selling it
     * @param merchantLocation the Location of the Merchant
     * @return a description of the purchase outcome
     */
    @Override
    public String purchase(Actor buyer, Actor merchant, Location merchantLocation) {
        buyer.modifyStatistic(FarmerStatistics.COINS, StatisticOperations.DECREASE, PRICE);
        if (random.nextInt(100) < FAIL_CHANCE_PERCENT) {
            return buyer + " pays " + merchant + ", who pockets the coins and hands over nothing.";
        }
        buyer.getInventory().add(new Sword());
        return buyer + " buys a Sword from " + merchant + ".";
    }
}