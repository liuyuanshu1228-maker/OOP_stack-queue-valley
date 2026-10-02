package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.statistics.FarmerStatistics;
import game.trading.Sellable;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Represents a Stone ('s') item that can be sold to the Merchant for 10 coins.
 * Selling a Stone also causes the Merchant to build a Ground on an adjacent tile.
 * The concrete Ground type is injected at construction time, so Stone has no
 * dependency on any specific Ground subclass.
 */
public class Stone extends Item implements Sellable {

    private static final int SELL_PRICE = 10;

    private final Random random = new Random();
    private final Supplier<Ground> groundFactory;

    /**
     * Constructor for Stone.
     *
     * @param groundFactory a factory that produces the Ground placed when this Stone is sold
     */
    public Stone(Supplier<Ground> groundFactory) {
        super("Stone", 's');
        this.makePortable();
        this.groundFactory = groundFactory;
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
     * a new Ground on a randomly chosen tile adjacent to the Merchant.
     *
     * @param seller           the Actor selling this item
     * @param merchant         the Actor buying it
     * @param merchantLocation the Location of the merchant
     * @return a description of the sale outcome
     */
    @Override
    public String sell(Actor seller, Actor merchant, Location merchantLocation) {
        seller.modifyStatistic(FarmerStatistics.COINS, StatisticOperations.INCREASE, SELL_PRICE);
        List<Location> nearby = merchantLocation.getNearbyLocations(1);
        String message = seller + " sells " + this + " to " + merchant + " for " + SELL_PRICE + " coins.";
        if (!nearby.isEmpty()) {
            nearby.get(random.nextInt(nearby.size())).setGround(groundFactory.get());
            message += " " + merchant + " builds a Rock nearby.";
        }
        return message;
    }
}
