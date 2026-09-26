package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Interface representing an item in an actor's inventory that can be sold to a buyer (e.g., Merchant).
 *
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
     *
     * @param seller           the Actor selling this item
     * @param merchant         the Actor buying it
     * @param merchantLocation the Location of the merchant
     * @return a description of the outcome
     */
    String sell(Actor seller, Actor merchant, Location merchantLocation);
}