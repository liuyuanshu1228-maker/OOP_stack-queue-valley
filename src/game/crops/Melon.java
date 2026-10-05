package game.crops;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.WaterCropAction;
import game.positions.LocationUtils;
import game.util.RandomUtils;

/**
 * A melon crop. Watering it may raise the farmer's maximum stamina, and it
 * can be harvested at any time, though harvesting risks an explosion that
 * damages everyone nearby.
 */
public class Melon extends Crop implements CropWaterable {

    /** Stamina consumed by a successful Melon harvest. */
    private static final int HARVEST_STAMINA_COST = 5;

    /** Stamina consumed by a successful Melon watering. */
    private static final int WATERING_STAMINA_COST = 1;

    /** Chance (out of 100) that harvesting causes the melon to explode. */
    private static final int EXPLOSION_CHANCE_PERCENT = 50;

    /** Base explosion damage before subtracting the watering count. */
    private static final int BASE_EXPLOSION_DAMAGE = 20;

    /** Chance (out of 100) that watering raises the actor's maximum stamina. */
    private static final int MAX_STAMINA_INCREASE_CHANCE_PERCENT = 50;

    /** Amount by which watering raises the actor's maximum stamina, when it does. */
    private static final int MAX_STAMINA_INCREASE_AMOUNT = 1;

    /**
     * Constructor.
     */
    public Melon() {
        super('M', "Melon");
    }

    /**
     * Offers a watering action, on top of the harvesting action inherited
     * from {@link Crop}, to an actor standing on or next to this crop.
     *
     * @see Crop#allowableActions(Actor, Location, String)
     * @param actor the actor acting
     * @param location the current location of this crop
     * @param direction the direction of this crop from the actor
     * @return the allowable actions for this crop
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        actions.add(new WaterCropAction(location));
        return actions;
    }

    /**
     * @return the stamina cost of harvesting a melon
     */
    @Override
    public int getHarvestStaminaCost() {
        return HARVEST_STAMINA_COST;
    }

    /**
     * Destroys the melon, then rolls the chance for it to explode. The
     * explosion's damage shrinks by one point for every time the melon has
     * been watered, down to a minimum of no damage at all.
     *
     * @param actor the actor performing the harvest
     * @param location the location of the crop being harvested
     * @return a description of the outcome
     */
    @Override
    public String harvest(Actor actor, Location location) {
        removeCrop(location);

        if (RandomUtils.rollPercent(EXPLOSION_CHANCE_PERCENT)) {
            int damage = Math.max(0, BASE_EXPLOSION_DAMAGE - getWateringCount());
            LocationUtils.hurtActorsAround(location, damage);
            return actor + " harvests the " + this + ". It explodes, dealing " + damage + " damage to everyone nearby!";
        }

        actor.getInventory().add(new MelonItem());
        return actor + " harvests the " + this + " and picks up a melon.";
    }

    /**
     * @return the stamina cost of watering a melon
     */
    @Override
    public int getWateringStaminaCost() {
        return WATERING_STAMINA_COST;
    }

    /**
     * Rolls the chance to raise the actor's maximum stamina.
     *
     * @param actor the actor watering the melon
     * @param location the location of the melon being watered
     * @return a description of what happened
     */
    @Override
    public String water(Actor actor, Location location) {
        String message = actor + " waters the " + this + ".";

        if (RandomUtils.rollPercent(MAX_STAMINA_INCREASE_CHANCE_PERCENT)) {
            int maxStaminaBefore = actor.getMaximumStatistic(ActorStatistics.STAMINA);
            actor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE, MAX_STAMINA_INCREASE_AMOUNT);
            int maxStaminaAfter = actor.getMaximumStatistic(ActorStatistics.STAMINA);
            message += " Its maximum stamina increases by " + (maxStaminaAfter - maxStaminaBefore)
                    + " (" + maxStaminaBefore + " -> " + maxStaminaAfter + ").";
        }

        return message;
    }

}
