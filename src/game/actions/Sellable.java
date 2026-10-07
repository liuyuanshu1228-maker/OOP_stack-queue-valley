package game.actions;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Interface representing an item in an actor's inventory that can be sold to a
 * buyer (e.g., Merchant).
 */
public interface Sellable {

    /**
     * Returns the base selling price of the item in coins.
     *
     * @return the selling price in coins
     */
    int getSellPrice();

    /**
     * Completes the sale of this item, including payment and any item-specific side effect.
     * <p>
     * Precondition: the item has already been removed from {@code seller}'s
     * inventory. The calling {@code SellAction} does this before calling, so
     * implementations do not remove it themselves.
     *
     * @param seller          the Actor selling this item
     * @param merchantContext the merchant buying it and its current Location
     * @return a description of the outcome
     */
    String sell(Actor seller, MerchantContext merchantContext);
}
