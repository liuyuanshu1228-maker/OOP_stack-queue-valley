package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.MerchantContext;
import game.actions.Purchasable;
import game.random.RandomUtils;
import game.statistics.FarmerStatistics;

/**
 * A Sword bought from the Merchant. Used to fight hostile creatures in
 * the mines — see REQ4 for its combat behaviour.
 * <p>
 * A Sword sells itself: the Merchant shows one on their stall, and buying it
 * hands the buyer a new Sword. Half the time the Merchant pockets the coins
 * and hands over nothing.
 * <p>
 * Uses '!' to avoid clashing with the Strawberry crop/item's 'S'/'s'.
 */
public class Sword extends Item implements Purchasable {

    /** The name a Sword is sold under by the Merchant. */
    private static final String PRODUCT_NAME = "Sword";

    /** Coins charged for a Sword. */
    private static final int PRICE = 3000;

    /** Chance (out of 100) that the Merchant keeps the coins and hands over nothing. */
    private static final int NOTHING_HANDED_OVER_CHANCE_PERCENT = 50;

    /** Decides whether the Merchant hands over the Sword, and is passed on to the Sword sold. */
    private final RandomUtils randomUtils;

    /**
     * Creates a new Sword. Initializes the portable Sword item with display character '!'.
     *
     * @param randomUtils decides whether the Merchant hands over a Sword that is bought
     */
    public Sword(RandomUtils randomUtils) {
        super(PRODUCT_NAME, '!');
        this.makePortable();
        this.randomUtils = randomUtils;
    }

    /**
     * @return the name a Sword is sold under
     */
    @Override
    public String getProductName() {
        return PRODUCT_NAME;
    }

    /**
     * @return the price of a Sword, in coins
     */
    @Override
    public int getPrice() {
        return PRICE;
    }

    /**
     * Charges the buyer, then half the time hands over a new Sword and
     * otherwise hands over nothing.
     *
     * @param buyer the actor buying the Sword
     * @param merchantContext the Merchant selling it and where they stand
     * @return a description of the purchase
     */
    @Override
    public String purchase(Actor buyer, MerchantContext merchantContext) {
        Actor merchant = merchantContext.getMerchant();
        buyer.modifyStatistic(FarmerStatistics.COINS, StatisticOperations.DECREASE, PRICE);
        if (randomUtils.rollPercent(NOTHING_HANDED_OVER_CHANCE_PERCENT)) {
            return buyer + " pays " + merchant + ", who pockets the coins and hands over nothing.";
        }
        buyer.getInventory().add(new Sword(randomUtils));
        return buyer + " buys a Sword from " + merchant + ".";
    }
}
