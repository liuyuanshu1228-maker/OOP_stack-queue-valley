package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.statistics.FarmerStatistics;
import game.trading.Sellable;


/**
 * Abstract base class for portable items that can be sold to a Merchant.
 * Encapsulates the common sale flow (payment and the base sale message) and lets
 * subclasses customize it through hooks: whether the Merchant refuses to pay,
 * and any item-specific side effect triggered by the sale.
 */
public abstract class SellableItem extends Item implements Sellable {

    private final int sellPrice;

    /**
     * Constructor for SellableItem. Initializes a portable item with the given sell price.
     *
     * @param name        the name of the item
     * @param displayChar the character used to display the item on the map
     * @param sellPrice   the number of coins the Merchant pays for the item
     */
    protected SellableItem(String name, char displayChar, int sellPrice) {
        super(name, displayChar);
        this.sellPrice = sellPrice;
        this.makePortable();
    }

    /**
     * Returns the base selling price of this item in coins.
     *
     * @return the selling price in coins
     */
    @Override
    public final int getSellPrice() {
        return sellPrice;
    }

    /**
     * Completes the sale of this item: unless the Merchant refuses to pay, the seller
     * is paid and any item-specific side effect is applied.
     *
     * @param seller           the Actor selling this item
     * @param merchant         the Actor buying it
     * @param merchantLocation the Location of the merchant
     * @return a description of the sale outcome
     */
    @Override
    public final String sell(Actor seller, Actor merchant, Location merchantLocation) {
        if (merchantRefusesPayment()) {
            return merchant + " takes the " + this + " from " + seller + " and pays nothing.";
        }
        seller.modifyStatistic(FarmerStatistics.COINS, StatisticOperations.INCREASE, sellPrice);
        String message = seller + " sells " + this + " to " + merchant + " for " + sellPrice + " coins.";
        String effect = applySaleEffect(seller, merchant, merchantLocation);
        return effect.isEmpty() ? message : message + " " + effect;
    }

    /**
     * Determines whether the Merchant takes this item without paying.
     * Defaults to {@code false}; subclasses may override to introduce a refusal chance.
     *
     * @return {@code true} if the Merchant pays nothing, {@code false} otherwise
     */
    protected boolean merchantRefusesPayment() {
        return false;
    }

    /**
     * Applies any item-specific side effect after the seller has been paid.
     * Defaults to no effect; subclasses may override.
     *
     * @param seller           the Actor selling this item
     * @param merchant         the Actor buying it
     * @param merchantLocation the Location of the merchant
     * @return a description of the side effect, or an empty string if nothing happened
     */
    protected String applySaleEffect(Actor seller, Actor merchant, Location merchantLocation) {
        return "";
    }
}
