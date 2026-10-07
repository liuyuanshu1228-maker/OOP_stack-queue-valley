package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.CropWaterable;
import game.positions.area.ItemDropper;
import game.random.RandomUtils;
import game.statistics.StatisticAdjuster;

/**
 * A parsnip crop. It can be watered, which heals the farmer but risks
 * ruining the plant, and it can be harvested at any time for a Parsnip,
 * with a chance of a bonus Parsnip appearing nearby.
 */
public class Parsnip extends Crop implements CropWaterable {

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

    /**
     * Constructor.
     */
    public Parsnip() {
        super('P', "Parsnip");
    }

    /**
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

        if (RandomUtils.rollPercent(BONUS_SPAWN_CHANCE_PERCENT)) {
            ItemDropper.adjacentTo(location).dropOnRandomTile(new ParsnipItem());
        }

        return actor + " harvests the " + this + ".";
    }

    /**
     * @return the stamina cost of watering a parsnip
     */
    @Override
    public int getWateringStaminaCost() {
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
        String message = actor + " waters the " + this + " and is healed for "
                + new StatisticAdjuster(actor).increaseAndDescribe(
                        ActorStatistics.HEALTH, HEAL_AMOUNT, "points") + ".";

        int damageChancePercent = BASE_DAMAGE_CHANCE_PERCENT + getWateringCount();
        if (RandomUtils.rollPercent(damageChancePercent)) {
            removeCrop(location);
            message += " The " + this + " couldn't take it and was destroyed.";
        }

        return message;
    }
}
