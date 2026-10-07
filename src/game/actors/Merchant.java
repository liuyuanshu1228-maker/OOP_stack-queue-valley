package game.actors;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.BuyAction;
import game.actions.MerchantContext;
import game.actions.Purchasable;
import game.actions.SellAction;
import game.actions.Sellable;
import java.util.Arrays;

/**
 * Represents a wandering Merchant ('$') who offers buying and selling actions
 * to adjacent actors on the map.
 *
 */
public class Merchant extends WanderingActor {

    /** The Merchant's starting (and maximum) hit points. */
    private static final int MAX_HP = 100;

    /** The set of products this Merchant offers for purchase. */
    private final Purchasable[] catalog;

    /**
     * Constructor for Merchant. The purchasable offerings are injected by the caller,
     * so Merchant depends only on the Purchasable abstraction rather than on concrete
     * products (Dependency Inversion), and different Merchants can carry different
     * catalogs without modifying this class (Open/Closed).
     *
     * @param offerings the purchasable products offered by this Merchant
     */
    public Merchant(Purchasable... offerings) {
        super("Merchant", '$', MAX_HP);
        this.catalog = Arrays.copyOf(offerings, offerings.length);
    }

    /**
     * Returns a collection of actions that an adjacent Actor can perform with this Merchant,
     * including buying products from the catalog and selling any {@link Sellable} items
     * currently in the other Actor's inventory.
     *
     * @param otherActor the Actor that might perform an action
     * @param direction  String representing the direction of the other Actor
     * @param map        current GameMap
     * @return a collection of available BuyAction and SellAction instances
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = new ActionList();
        MerchantContext merchantContext = new MerchantContext(this, map.locationOf(this));

        for (Purchasable offering : catalog) {
            actions.add(new BuyAction(offering, merchantContext));
        }

        for (Item item : otherActor.getInventory().getItems()) {
            item.asCapability(Sellable.class).ifPresent(sellable ->
                    actions.add(new SellAction(item, sellable, merchantContext)));
        }
        return actions;
    }
}
