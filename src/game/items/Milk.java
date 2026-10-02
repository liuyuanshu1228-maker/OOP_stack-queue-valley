package game.items;

/**
 * Represents a Milk ('K') item produced by a Cow. Can be sold to the Merchant
 * for 60 coins, with a 50% chance that the Merchant takes the Milk and pays nothing.
 */
public class Milk extends SellableItem {

    private static final int SELL_PRICE = 60;
    private static final int NO_PAYMENT_CHANCE_PERCENT = 50;

    /**
     * Constructor for Milk. Initializes the portable Milk item with display character 'K'.
     */
    public Milk() {
        super("Milk", 'K', SELL_PRICE);
    }

    /**
     * Rolls a 50% chance for the Merchant to take the Milk without paying.
     *
     * @return {@code true} if the Merchant pays nothing, {@code false} otherwise
     */
    @Override
    protected boolean merchantRefusesPayment() {
        return rollPercent(NO_PAYMENT_CHANCE_PERCENT);
    }
}
