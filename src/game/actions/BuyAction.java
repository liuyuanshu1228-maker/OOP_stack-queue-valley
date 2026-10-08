package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.statistics.FarmerStatistics;
import game.statistics.StatisticAdjuster;

/**
 * An Action that allows an Actor to buy a {@link Purchasable} product from a
 * Merchant, provided they have enough coins.
 */
public class BuyAction extends Action {

    /** The product being purchased. */
    private final Purchasable purchasable;

    /** The Merchant selling the product, and its current Location. */
    private final MerchantContext merchantContext;

    /**
     * Creates a new BuyAction.
     *
     * @param purchasable the Purchasable product being offered
     * @param merchantContext the Merchant selling the product and its current Location
     */
    public BuyAction(Purchasable purchasable, MerchantContext merchantContext) {
        this.purchasable = purchasable;
        this.merchantContext = merchantContext;
    }

    /**
     * Checks whether the actor has enough coins to purchase the product and,
     * if so, executes the purchase logic.
     *
     * @param actor the Actor performing the action
     * @param map the map the Actor is on
     * @return a description of what happened suitable for display
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        StatisticAdjuster buyer = new StatisticAdjuster(actor);
        if (!buyer.hasEnough(FarmerStatistics.COINS, purchasable.getPrice())) {
            return actor + " doesn't have enough coins to buy "
                    + purchasable.getProductName() + ".";
        }
        return purchasable.purchase(actor, merchantContext);
    }

    /**
     * Returns a descriptive string for this purchase action to display in the menu.
     *
     * @param actor the Actor performing the action
     * @return the text to put on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " buys " + purchasable.getProductName()
                + " for " + purchasable.getPrice() + " coins.";
    }
}
