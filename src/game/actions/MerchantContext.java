package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * The merchant side of a trade: the Actor acting as the merchant, and the
 * merchant's current Location. Every sell/purchase hook that needs the
 * merchant's position also needs the merchant Actor (for payment, messages,
 * or both), so the two are bundled into one immutable value instead of being
 * passed as two separate parameters that would otherwise have to travel
 * together, in the same order, through every signature in the trading/selling
 * chain (Sellable, Purchasable, SellableItem, CropPurchase, SellAction, BuyAction
 * and their subclasses).
 */
public class MerchantContext {

    /** The Actor acting as the merchant in this trade. */
    private final Actor merchant;

    /** The merchant's current Location. */
    private final Location location;

    /**
     * Constructor.
     *
     * @param merchant the Actor acting as the merchant in this trade
     * @param location the merchant's current Location
     */
    public MerchantContext(Actor merchant, Location location) {
        this.merchant = merchant;
        this.location = location;
    }

    /**
     * Returns the Actor acting as the merchant in this trade.
     *
     * @return the merchant Actor
     */
    public Actor getMerchant() {
        return merchant;
    }

    /**
     * Returns the merchant's current Location.
     *
     * @return the merchant's Location
     */
    public Location getLocation() {
        return location;
    }
}
