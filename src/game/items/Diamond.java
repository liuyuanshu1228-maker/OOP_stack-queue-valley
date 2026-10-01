package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.statistics.FarmerStatistics;
import game.trading.Sellable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Represents a Diamond ('d') item that can be sold to the Merchant for 5000 coins,
 * with a 50% chance that the Merchant also steals a random item from the seller's inventory.
 */
public class Diamond extends Item implements Sellable {

    private static final int SELL_PRICE = 5000;
    private static final int THEFT_CHANCE_PERCENT = 50;

    private final Random random = new Random();

    /**
     * Constructor for Diamond. Initializes the portable Diamond item with display character 'd'.
     */
    public Diamond() {
        super("Diamond", 'd');
        this.makePortable();
    }

    /**
     * Returns the base selling price of the Diamond in coins.
     *
     * @return the selling price in coins
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Completes the sale of this Diamond, awarding coins to the seller and rolling
     * a 50% chance for the Merchant to steal a random item from the seller's inventory.
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

        if (random.nextInt(100) < THEFT_CHANCE_PERCENT) {
            List<Item> items = seller.getInventory().getItems();
            Optional<Item> stolen = items.isEmpty()
                    ? Optional.empty()
                    : Optional.of(items.get(random.nextInt(items.size())));
            stolen.ifPresent(item -> {
                seller.getInventory().remove(item);
                message.append(" ").append(merchant).append(" also steals ").append(item).append(".");
            });
        }
        return message.toString();
    }
}