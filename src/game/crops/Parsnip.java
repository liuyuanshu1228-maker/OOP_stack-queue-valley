package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.MerchantContext;
import game.actions.Purchasable;
import game.plants.Produce;
import game.plants.Producing;
import game.positions.Waterable;
import game.positions.area.AreaFactory;
import game.positions.area.ItemDropper;
import game.random.RandomUtils;
import game.statistics.FarmerStatistics;
import game.statistics.StatisticAdjuster;

import java.util.Optional;

/**
 * A parsnip crop. It can be watered, which heals the farmer but risks
 * ruining the plant, and it can be harvested at any time for a Parsnip,
 * with a chance of a bonus Parsnip appearing nearby.
 * <p>
 * Every {@value #PRODUCE_INTERVAL} turns it drops a parsnip on an adjacent
 * tile by itself. It is mature only and has no earlier stage, so when it is
 * regressed it dies, leaving Dirt.
 */
public class Parsnip extends Crop implements Waterable, Purchasable, Producing {

    /** Stamina consumed by a successful Parsnip harvest. */
    private static final int HARVEST_STAMINA_COST = 1;

    /** Stamina consumed by a successful Parsnip watering. */
    private static final int WATERING_STAMINA_COST = 1;

    /** HP restored to the actor by a successful watering. */
    private static final int HEAL_AMOUNT = 20;

    /** Base chance (out of 100) that a watering damages and destroys the plant. */
    private static final int BASE_DAMAGE_CHANCE_PERCENT = 10;

    /** Chance (out of 100) that a second Parsnip spawns on an adjacent tile. */
    private static final int BONUS_SPAWN_CHANCE_PERCENT = 50;

    /** How many turns between parsnips dropped by the plant itself. */
    private static final int PRODUCE_INTERVAL = 15;

    /** The name a Parsnip crop is sold under by the Merchant. */
    private static final String PRODUCT_NAME = "Parsnip crop";

    /** Coins charged for a Parsnip crop. */
    private static final int PRICE = 250;

    /** Number of Parsnips dropped next to a Parsnip crop when it is bought. */
    private static final int PARSNIPS_GIVEN_ON_PURCHASE = 3;

    /** The randomness used by this crop's own chances. */
    private final RandomUtils randomUtils;

    /** Builds Areas for this crop's own production and bonus spawning. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new Parsnip.
     *
     * @param randomUtils the randomness used to roll this crop's chances
     * @param areaFactory builds Areas for this crop's production and bonus spawning
     */
    public Parsnip(RandomUtils randomUtils, AreaFactory areaFactory) {
        super('P', "Parsnip", new Produce(PRODUCE_INTERVAL, randomUtils, areaFactory));
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * A Parsnip produces a new ParsnipItem.
     *
     * @return a new ParsnipItem
     */
    @Override
    public Item produce() {
        return new ParsnipItem();
    }

    /**
     * How much stamina a successful harvest of this parsnip costs.
     *
     * @return the stamina cost of harvesting a parsnip
     */
    @Override
    public int getHarvestStaminaCost() {
        return HARVEST_STAMINA_COST;
    }

    /**
     * Adds one Parsnip to the actor's inventory, replaces the ground with
     * dirt, and has a 50% chance of placing a second Parsnip on a tile
     * adjacent to this crop's former location.
     *
     * @param actor the actor performing the harvest
     * @param location the location of the crop being harvested
     * @return a description of the outcome
     */
    @Override
    public String harvest(Actor actor, Location location) {
        actor.getInventory().add(new ParsnipItem());
        removeCrop(location);

        if (randomUtils.rollPercent(BONUS_SPAWN_CHANCE_PERCENT)) {
            new ItemDropper(areaFactory.adjacentTo(location)).dropOnRandomTile(new ParsnipItem());
        }

        return actor + " harvests the " + this + ".";
    }

    /**
     * Watering a parsnip has its own cost, so the tool's cost is ignored.
     *
     * @param toolCost the stamina cost of the watering tool (not used)
     * @return the stamina cost of watering a parsnip
     */
    @Override
    public int getWateringStaminaCost(int toolCost) {
        return WATERING_STAMINA_COST;
    }

    /**
     * Heals the actor, then rolls the chance for the parsnip to be damaged
     * and removed from the ground. The chance grows by one percentage point
     * for every time the parsnip has been watered so far, including this
     * watering.
     *
     * @param actor the actor watering the parsnip
     * @param location the location of the parsnip being watered
     * @return a description of what happened
     */
    @Override
    public String water(Actor actor, Location location) {
        recordWatering();
        String message = actor + " waters the " + this + " and is healed for "
                + new StatisticAdjuster(actor).increase(
                        ActorStatistics.HEALTH, HEAL_AMOUNT).describe("points") + ".";

        int damageChancePercent = BASE_DAMAGE_CHANCE_PERCENT + getWateringCount();
        if (randomUtils.rollPercent(damageChancePercent)) {
            removeCrop(location);
            message += " The " + this + " couldn't take it and was destroyed.";
        }

        return message;
    }

    /**
     * @return the name a Parsnip crop is sold under
     */
    @Override
    public String getProductName() {
        return PRODUCT_NAME;
    }

    /**
     * @return the price of a Parsnip crop, in coins
     */
    @Override
    public int getPrice() {
        return PRICE;
    }

    /**
     * Has the Merchant plant a new Parsnip next to them, charges the buyer, and
     * drops three Parsnips around the new crop. Nothing is charged if there is
     * nowhere to plant it.
     *
     * @param buyer the actor buying the crop
     * @param merchantContext the Merchant selling it and where they stand
     * @return a description of the purchase
     */
    @Override
    public String purchase(Actor buyer, MerchantContext merchantContext) {
        Actor merchant = merchantContext.getMerchant();
        Optional<Location> planted = plantNextTo(new Parsnip(randomUtils, areaFactory),
                merchantContext.getLocation(), areaFactory);
        if (planted.isEmpty()) {
            return noRoomToPlant(buyer, merchant, PRODUCT_NAME);
        }
        buyer.modifyStatistic(FarmerStatistics.COINS, StatisticOperations.DECREASE, PRICE);
        ItemDropper dropper = new ItemDropper(areaFactory.adjacentTo(planted.get()));
        for (int i = 0; i < PARSNIPS_GIVEN_ON_PURCHASE; i++) {
            dropper.dropOnRandomTile(new ParsnipItem());
        }
        return buyer + " buys a " + PRODUCT_NAME + " from " + merchant + ", planted at " + planted.get() + ".";
    }

}
