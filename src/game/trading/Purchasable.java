package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Interface representing an item or crop that can be purchased from a seller (e.g., Merchant).
 */
public interface Purchasable {

    /**
     * Returns the display name of this purchasable product.
     *
     * @return a short display name, e.g. "Tomato crop"
     */
    String getProductName();

    /**
     * Returns the base purchase price in coins.
     *
     * @return the price in coins
     */
    int getPrice();

    /**
     * Applies the effect of a successful purchase: charges the buyer
     * (or waives payment) and delivers the product.
     *
     * @param buyer            the Actor buying this product
     * @param merchant         the Merchant selling it
     * @param merchantLocation the Location of the Merchant
     * @return a description of the outcome
     */
    String purchase(Actor buyer, Actor merchant, Location merchantLocation);
}