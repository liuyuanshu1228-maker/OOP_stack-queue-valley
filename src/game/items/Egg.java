package game.items;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Chicken;
import game.positions.LocationUtils;
import game.statistics.FarmerStatistics;
import game.trading.Sellable;

import java.util.Random;

/**
 * Represents an Egg ('0') item produced by a Chicken. Can be sold to the Merchant
 * for 40 coins, with a 50% chance to spawn a new Chicken near the Merchant.
 */
public class Egg extends Item implements Sellable {

    private static final int SELL_PRICE = 40;
    private static final int CHICKEN_SPAWN_CHANCE_PERCENT = 50;

    private final Random random = new Random();

    /**
     * Constructor for Egg. Initializes the portable Egg item with display character '0'.
     */
    public Egg() {
        super("Egg", '0');
        this.makePortable();
    }

    /**
     * Returns the base selling price of the Egg in coins.
     *
     * @return the selling price in coins
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Completes the sale of this Egg, awarding coins to the seller and rolling
     * a 50% chance for a Chicken to spawn on an enterable tile near the Merchant.
     *
     * @param seller           the Actor selling this item
     * @param merchant         the Actor buying it
     * @param merchantLocation the Location of the merchant
     * @return a description of the sale outcome
     */
    @Override
    public String sell(Actor seller, Actor merchant, Location merchantLocation) {
        seller.modifyStatistic(FarmerStatistics.COINS, StatisticOperations.INCREASE, SELL_PRICE);
        StringBuilder message = new StringBuilder(
                seller + " sells " + this + " to " + merchant + " for " + SELL_PRICE + " coins.");

        if (random.nextInt(100) < CHICKEN_SPAWN_CHANCE_PERCENT) {
            Chicken chicken = new Chicken("Chicken");
            var spot = LocationUtils.randomEnterableNearby(merchantLocation, chicken);
            if (spot.isPresent()) {
                try {
                    spot.get().addActor(chicken);
                    message.append(" A chicken appears nearby.");
                } catch (GameEngineException e) {
                    // Location was already verified as enterable and unoccupied
                }
            }
        }
        return message.toString();
    }
}