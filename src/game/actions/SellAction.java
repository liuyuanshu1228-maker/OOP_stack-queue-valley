package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An Action that allows an Actor to sell a {@link Sellable} Item to a Merchant.
 *
 */
public class SellAction extends Action {

    /** The item being sold from the actor's inventory. */
    private final Item item;

    /** The Sellable capability that defines the sell price and sell logic. */
    private final Sellable sellable;

    /** The Merchant buying the item, and its current Location. */
    private final MerchantContext merchantContext;

    /**
     * Creates a new SellAction.
     *
     * @param item the Item in the actor's inventory to be sold
     * @param sellable the Sellable capability of the item
     * @param merchantContext the Merchant buying the item and its current Location
     */
    public SellAction(Item item, Sellable sellable, MerchantContext merchantContext) {
        this.item = item;
        this.sellable = sellable;
        this.merchantContext = merchantContext;
    }

    /**
     * Removes the sold item from the actor's inventory and executes the sale logic.
     *
     * @param actor the Actor performing the action
     * @param map the map the Actor is on
     * @return a description of the sale outcome
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        actor.getInventory().remove(item);
        return sellable.sell(actor, merchantContext);
    }

    /**
     * Returns a descriptive string for this sell action to display in the menu.
     *
     * @param actor the Actor performing the action
     * @return the text to put on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " sells " + item + " to " + merchantContext.getMerchant()
                + " for " + sellable.getSellPrice() + " coins.";
    }
}
