package game.actors;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BuyAction;
import game.actions.SellAction;
import game.trading.ParsnipPurchase;
import game.trading.Purchasable;
import game.trading.Sellable;
import game.trading.SwordPurchase;
import game.trading.TomatoPurchase;

/**
 * Represents a wandering Merchant ('$') who offers buying and selling actions
 * to adjacent actors on the map.
 *
 */
public class Merchant extends WanderingActor {

    private static final int MAX_HP = 100;

    private final Purchasable[] catalog = {
        new ParsnipPurchase(),
        new TomatoPurchase(),
        new SwordPurchase()
    };

    /**
     * Constructor for Merchant. Initializes the Merchant with display character '$'
     * and 100 hit points.
     */
    public Merchant() {
        super("Merchant", '$', MAX_HP);
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
        Location merchantLocation = map.locationOf(this);

        for (Purchasable offering : catalog) {
            actions.add(new BuyAction(offering, this, merchantLocation));
        }

        for (Item item : otherActor.getInventory().getItems()) {
            item.asCapability(Sellable.class).ifPresent(sellable ->
                    actions.add(new SellAction(item, sellable, this, merchantLocation)));
        }
        return actions;
    }
}