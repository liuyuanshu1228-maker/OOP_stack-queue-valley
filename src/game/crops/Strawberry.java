package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.plants.Produce;
import game.positions.area.ItemDropper;
import game.random.RandomUtils;
import game.positions.Waterable;

/**
 * A strawberry crop. It can be watered, which scatters strawberries onto
 * adjacent tiles, and it can be harvested at any time for strawberries,
 * though harvesting risks destroying the plant.
 * <p>
 * Every {@value #PRODUCE_INTERVAL} turns it drops a strawberry on an adjacent
 * tile by itself, which may be rotten. When it is regressed it goes back to
 * being a {@link StrawberrySprout}.
 */
public class Strawberry extends Crop implements Waterable {

    /** Stamina consumed by a successful Strawberry harvest. */
    private static final int HARVEST_STAMINA_COST = 1;

    /** Stamina consumed by a successful Strawberry watering. */
    private static final int WATERING_STAMINA_COST = 1;

    /** Number of strawberries produced by a harvest. */
    private static final int STRAWBERRIES_PER_HARVEST = 2;

    /**
     * Base chance (out of 100) that harvesting destroys the plant, before subtracting the
     * watering count.
     */
    private static final int BASE_DESTRUCTION_CHANCE_PERCENT = 50;

    /** Number of strawberries dropped nearby by a single watering. */
    private static final int STRAWBERRIES_PER_WATERING = 2;

    /** How many turns between strawberries dropped by the plant itself. */
    private static final int PRODUCE_INTERVAL = 5;

    /**
     * Creates a new Strawberry.
     */
    public Strawberry() {
        super('S', "Strawberry", new Produce(PRODUCE_INTERVAL));
    }

    /**
     * A Strawberry produces a new (possibly rotten) StrawberryItem.
     *
     * @return a new StrawberryItem
     */
    @Override
    protected Item produce() {
        return StrawberryItem.createRandom();
    }

    /**
     * A regressed Strawberry becomes a new Strawberry Sprout.
     *
     * @return a new StrawberrySprout
     */
    @Override
    protected Ground previousStage() {
        return new StrawberrySprout();
    }

    /**
     * How much stamina a successful harvest of this strawberry costs.
     *
     * @return the stamina cost of harvesting a strawberry
     */
    @Override
    public int getHarvestStaminaCost() {
        return HARVEST_STAMINA_COST;
    }

    /**
     * Adds two strawberries to the actor's inventory, then rolls the chance
     * for the plant to be destroyed. The chance shrinks by one percentage
     * point for every time the strawberry has been watered.
     *
     * @param actor the actor performing the harvest
     * @param location the location of the crop being harvested
     * @return a description of the outcome
     */
    @Override
    public String harvest(Actor actor, Location location) {
        for (int i = 0; i < STRAWBERRIES_PER_HARVEST; i++) {
            actor.getInventory().add(StrawberryItem.createRandom());
        }

        String message = actor + " harvests the " + this + " and gets "
                + STRAWBERRIES_PER_HARVEST + " strawberries.";

        int destructionChancePercent = BASE_DESTRUCTION_CHANCE_PERCENT - getWateringCount();
        if (RandomUtils.rollPercent(destructionChancePercent)) {
            removeCrop(location);
            message += " The " + this + " couldn't withstand the harvest and was destroyed.";
        }

        return message;
    }

    /**
     * Watering a strawberry has its own cost, so the tool's cost is ignored.
     *
     * @param toolCost the stamina cost of the watering tool (not used)
     * @return the stamina cost of watering a strawberry
     */
    @Override
    public int getWateringStaminaCost(int toolCost) {
        return WATERING_STAMINA_COST;
    }

    /**
     * Drops two strawberries onto random tiles adjacent to the crop.
     *
     * @param actor the actor watering the strawberry
     * @param location the location of the strawberry being watered
     * @return a description of what happened
     */
    @Override
    public String water(Actor actor, Location location) {
        recordWatering();
        for (int i = 0; i < STRAWBERRIES_PER_WATERING; i++) {
            ItemDropper.adjacentTo(location).dropOnRandomTile(StrawberryItem.createRandom());
        }

        return actor + " waters the " + this + ". Strawberries appear on the nearby tiles.";
    }
}
