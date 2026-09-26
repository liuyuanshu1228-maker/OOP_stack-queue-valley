package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.trading.Sellable;

/**
 * An Action that allows an Actor to sell a {@link Sellable} Item to a Merchant.
 *
 */
public class SellAction extends Action {

    private final Item item;
    private final Sellable sellable;
    private final Actor merchant;
    private final Location merchantLocation;

    /**
     * Constructor for SellAction.
     *
     * @param item             the Item in the actor's inventory to be sold
     * @param sellable         the Sellable capability of the item
     * @param merchant         the Merchant actor buying the item
     * @param merchantLocation the current Location of the Merchant
     */
    public SellAction(Item item, Sellable sellable, Actor merchant, Location merchantLocation) {
        this.item = item;
        this.sellable = sellable;
        this.merchant = merchant;
        this.merchantLocation = merchantLocation;
    }

    /**
     * Removes the sold item from the actor's inventory and executes the sale logic.
     *
     * @param actor the Actor performing the action
     * @param map   the map the Actor is on
     * @return a description of the sale outcome
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        actor.getInventory().remove(item);
        return sellable.sell(actor, merchant, merchantLocation);
    }

    /**
     * Returns a descriptive string for this sell action to display in the menu.
     *
     * @param actor the Actor performing the action
     * @return the text to put on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " sells " + item + " to " + merchant + " for " + sellable.getSellPrice() + " coins.";
    }
}