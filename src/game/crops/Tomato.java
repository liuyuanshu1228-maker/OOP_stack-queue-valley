package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.MerchantContext;
import game.actions.Purchasable;
import game.plants.Plant;
import game.plants.Produce;
import game.plants.Producing;
import game.plants.SelfReplicate;
import game.plants.Spreading;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;
import game.statistics.FarmerStatistics;
import game.statistics.StatisticAdjuster;

import java.util.Optional;

/**
 * A tomato crop. Unlike the other crops in this world, it can only be
 * harvested, not watered.
 * <p>
 * It can do two things by itself, in priority order:
 * <ol>
 *     <li>every {@value #PRODUCE_INTERVAL} turns, dropping a tomato on an
 *     adjacent tile;</li>
 *     <li>every {@value #SPREAD_INTERVAL} turns, a
 *     {@value #SPREAD_CHANCE_PERCENT}% chance of planting another Tomato on
 *     an adjacent tile.</li>
 * </ol>
 * It is mature only and has no earlier stage, so when it is regressed it dies
 * and leaves Dirt.
 */
public class Tomato extends Crop implements Purchasable, Producing, Spreading {

    /** Stamina consumed by a successful Tomato harvest. */
    private static final int HARVEST_STAMINA_COST = 2;

    /** Number of Tomatoes produced by a successful harvest. */
    private static final int TOMATOES_PRODUCED = 2;

    /** Increase to the actor's maximum stamina granted by a successful harvest. */
    private static final int MAX_STAMINA_INCREASE = 1;

    /** How many turns between tomatoes dropped by the plant itself. */
    private static final int PRODUCE_INTERVAL = 7;

    /** How many turns between attempts to plant a tomato nearby. */
    private static final int SPREAD_INTERVAL = 20;

    /** The chance, as a percentage, of planting a tomato nearby on its turn. */
    private static final int SPREAD_CHANCE_PERCENT = 10;

    /** The name a Tomato crop is sold under by the Merchant. */
    private static final String PRODUCT_NAME = "Tomato crop";

    /** Coins charged for a Tomato crop. */
    private static final int PRICE = 500;

    /** Chance (out of 100) that the Merchant plants a bought Tomato for free. */
    private static final int FREE_PLANTING_CHANCE_PERCENT = 50;

    /** The randomness this plant hands to its own abilities and its offspring. */
    private final RandomUtils randomUtils;

    /** Builds Areas for this plant's own abilities and its offspring. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new Tomato.
     *
     * @param randomUtils the randomness used by this plant's production and
     *                    spreading, and handed to its offspring
     * @param areaFactory builds Areas for this plant's production and
     *                    spreading, and handed to its offspring
     */
    public Tomato(RandomUtils randomUtils, AreaFactory areaFactory) {
        super('T', "Tomato",
                new Produce(PRODUCE_INTERVAL, randomUtils, areaFactory),
                new SelfReplicate(SPREAD_INTERVAL, SPREAD_CHANCE_PERCENT, randomUtils, areaFactory));
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * A Tomato produces a new TomatoItem.
     *
     * @return a new TomatoItem
     */
    @Override
    public Item produce() {
        return new TomatoItem();
    }

    /**
     * A Tomato spreads by planting another Tomato nearby.
     *
     * @return a new Tomato
     */
    @Override
    public Plant offspring() {
        return new Tomato(randomUtils, areaFactory);
    }

    /**
     * How much stamina a successful harvest of this tomato costs.
     *
     * @return the stamina cost of harvesting a tomato
     */
    @Override
    public int getHarvestStaminaCost() {
        return HARVEST_STAMINA_COST;
    }

    /**
     * Adds two tomatoes to the actor's inventory, replaces the ground with
     * dirt, and increases the actor's maximum stamina by 1 point without
     * restoring the current stamina value.
     *
     * @param actor the actor performing the harvest
     * @param location the location of the crop being harvested
     * @return a description of the outcome
     */
    @Override
    public String harvest(Actor actor, Location location) {
        for (int i = 0; i < TOMATOES_PRODUCED; i++) {
            actor.getInventory().add(new TomatoItem());
        }
        removeCrop(location);

        new StatisticAdjuster(actor).increaseMaximumWithoutRestoring(
                ActorStatistics.STAMINA, MAX_STAMINA_INCREASE);

        return actor + " harvests the " + this + ".";
    }

    /**
     * @return the name a Tomato crop is sold under
     */
    @Override
    public String getProductName() {
        return PRODUCT_NAME;
    }

    /**
     * @return the price of a Tomato crop, in coins
     */
    @Override
    public int getPrice() {
        return PRICE;
    }

    /**
     * Has the Merchant plant a new Tomato next to them. Half the time the
     * Merchant plants it for free; otherwise the buyer is charged. Nothing is
     * charged if there is nowhere to plant it.
     *
     * @param buyer the actor buying the crop
     * @param merchantContext the Merchant selling it and where they stand
     * @return a description of the purchase
     */
    @Override
    public String purchase(Actor buyer, MerchantContext merchantContext) {
        Actor merchant = merchantContext.getMerchant();
        Optional<Location> planted = plantNextTo(new Tomato(randomUtils, areaFactory),
                merchantContext.getLocation(), areaFactory);
        if (planted.isEmpty()) {
            return noRoomToPlant(buyer, merchant, PRODUCT_NAME);
        }
        String message = buyer + " buys a " + PRODUCT_NAME + " from " + merchant + ", planted at " + planted.get() + ".";
        if (randomUtils.rollPercent(FREE_PLANTING_CHANCE_PERCENT)) {
            return message + " " + merchant + " plants it for free, out of generosity.";
        }
        buyer.modifyStatistic(FarmerStatistics.COINS, StatisticOperations.DECREASE, PRICE);
        return message;
    }

}
