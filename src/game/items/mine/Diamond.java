package game.items.mine;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import game.actions.MerchantContext;
import game.items.SellableItem;
import game.random.RandomUtils;

import java.util.List;

/**
 * Represents a Diamond ('d') item that can be sold to the Merchant for 5000 coins,
 * with a 50% chance that the Merchant also steals a random item from the seller's inventory.
 */
public class Diamond extends SellableItem {

    /** Coins paid by the Merchant when a Diamond is sold. */
    private static final int SELL_PRICE = 5000;
    /** Percentage chance that the Merchant steals a random item from the seller after payment. */
    private static final int THEFT_CHANCE_PERCENT = 50;

    /**
     * Creates a new Diamond. Initializes the portable Diamond item with display character 'd'.
     */
    public Diamond() {
        super("Diamond", 'd', SELL_PRICE);
    }

    /**
     * Rolls a 50% chance for the Merchant to steal a random item from the seller's inventory.
     * The stolen item is removed from the game.
     *
     * @param seller the Actor selling this item
     * @param merchantContext the Merchant buying it and its current Location
     * @return a description of the theft, or an empty string if nothing was stolen
     */
    @Override
    protected String applySaleEffect(Actor seller, MerchantContext merchantContext) {
        List<Item> items = seller.getInventory().getItems();
        if (items.isEmpty() || !RandomUtils.rollPercent(THEFT_CHANCE_PERCENT)) {
            return "";
        }
        Item stolen = RandomUtils.pickRandom(items);
        seller.getInventory().remove(stolen);
        return merchantContext.getMerchant() + " also steals " + stolen + ".";
    }
}
