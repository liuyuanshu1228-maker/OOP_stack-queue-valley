package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.Chance;
import game.positions.Rock;
import game.statistics.FarmerStatistics;
import game.trading.Sellable;

/**
 * Represents a Stone ('s') item that can be sold to the Merchant for 10 coins.
 * Selling a Stone also causes the Merchant to build a Rock on an adjacent tile.
 */
public class Stone extends Item implements Sellable {

    private static final int SELL_PRICE = 10;

    /**
     * Constructor for Stone. Initializes the portable Stone item with display character 's'.
     */
    public Stone() {
        super("Stone", 's');
        this.makePortable();
    }

    /**
     * Returns the base selling price of the Stone in coins.
     *
     * @return the selling price in coins
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Completes the sale of this Stone, awarding coins to the seller and placing
     * a new Rock on a randomly chosen tile adjacent to the Merchant.
     *
     * @param seller           the Actor selling this item
     * @param merchant         the Actor buying it
     * @param merchantLocation the Location of the merchant
     * @return a description of the sale outcome
     */
    @Override
    public String sell(Actor seller, Actor merchant, Location merchantLocation) {
        seller.modifyStatistic(FarmerStatistics.COINS, StatisticOperations.INCREASE, SELL_PRICE);
        boolean built = Chance.pickOne(merchantLocation.getNearbyLocations(1))
                .map(target -> {
                    target.setGround(new Rock());
                    return true;
                })
                .orElse(false);

        return seller + " sells " + this + " to " + merchant + " for " + SELL_PRICE + " coins."
                + (built ? " " + merchant + " builds a Rock nearby." : "");
    }
}