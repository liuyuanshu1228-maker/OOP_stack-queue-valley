package game.items;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.positions.LocationUtils;
import game.statistics.FarmerStatistics;
import game.trading.Sellable;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Represents an Egg ('0') item produced by a Chicken. Can be sold to the Merchant
 * for 40 coins, with a 50% chance to spawn an animal near the Merchant.
 * The concrete Actor type that spawns is injected at construction time, so Egg
 * has no dependency on any specific Actor subclass.
 */
public class Egg extends Item implements Sellable {

    private static final int SELL_PRICE = 40;
    private static final int ANIMAL_SPAWN_CHANCE_PERCENT = 50;

    private final Random random = new Random();
    private final Supplier<Actor> animalFactory;

    /**
     * Constructor for Egg.
     *
     * @param animalFactory a factory that produces the Actor spawned when this Egg is sold
     */
    public Egg(Supplier<Actor> animalFactory) {
        super("Egg", '0');
        this.makePortable();
        this.animalFactory = animalFactory;
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
     * a 50% chance for an animal to spawn on an enterable tile near the Merchant.
     *
     * @param seller           the Actor selling this item
     * @param merchant         the Actor buying it
     * @param merchantLocation the Location of the merchant
     * @return a description of the sale outcome
     */
    @Override
    public String sell(Actor seller, Actor merchant, Location merchantLocation) {
        seller.modifyStatistic(FarmerStatistics.COINS, StatisticOperations.INCREASE, SELL_PRICE);
        String message = seller + " sells " + this + " to " + merchant + " for " + SELL_PRICE + " coins.";
        if (random.nextInt(100) < ANIMAL_SPAWN_CHANCE_PERCENT) {
            Actor animal = animalFactory.get();
            var spot = LocationUtils.randomEnterableNearby(merchantLocation, animal);
            if (spot.isPresent()) {
                try {
                    spot.get().addActor(animal);
                    message += " A chicken appears nearby.";
                } catch (GameEngineException e) {
                    // spot was verified as enterable but addActor failed
                }
            }
        }
        return message;
    }
}
