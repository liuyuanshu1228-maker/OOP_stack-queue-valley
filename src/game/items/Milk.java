package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.Chance;
import game.statistics.FarmerStatistics;
import game.trading.Sellable;

/**
 * Represents a Milk ('K') item produced by a Cow. Can be sold to the Merchant
 * for 60 coins, with a 50% chance that the Merchant takes the Milk and pays nothing.
 */
public class Milk extends Item implements Sellable {

    private static final int SELL_PRICE = 60;
    private static final int NO_PAYMENT_CHANCE_PERCENT = 50;

    /**
     * Constructor for Milk. Initializes the portable Milk item with display character 'K'.
     */
    public Milk() {
        super("Milk", 'K');
        this.makePortable();
    }

    /**
     * Returns the base selling price of the Milk in coins.
     *
     * @return the selling price in coins
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Completes the sale of this Milk, either awarding coins to the seller or
     * resulting in the Merchant taking the Milk without paying (50% chance).
     *
     * @param seller           the Actor selling this item
     * @param merchant         the Actor buying it
     * @param merchantLocation the Location of the merchant
     * @return a description of the sale outcome
     */
    @Override
    public String sell(Actor seller, Actor merchant, Location merchantLocation) {
        if (Chance.rollPercent(NO_PAYMENT_CHANCE_PERCENT)) {
            return merchant + " takes the " + this + " from " + seller + " and pays nothing.";
        }
        seller.modifyStatistic(FarmerStatistics.COINS, StatisticOperations.INCREASE, SELL_PRICE);
        return seller + " sells " + this + " to " + merchant + " for " + SELL_PRICE + " coins.";
    }
}