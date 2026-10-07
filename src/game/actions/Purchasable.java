package game.actions;

import edu.monash.fit2099.engine.actors.Actor;

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
     * <p>
     * Precondition: {@code buyer} has at least {@link #getPrice()} coins. The
     * calling {@code BuyAction} checks this before calling, so implementations
     * do not check it again.
     *
     * @param buyer           the Actor buying this product
     * @param merchantContext the Merchant selling it and its current Location
     * @return a description of the outcome
     */
    String purchase(Actor buyer, MerchantContext merchantContext);
}
