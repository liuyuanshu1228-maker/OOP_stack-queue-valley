package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import game.statistics.StatisticAdjuster;

/**
 * A tomato crop. Unlike the other crops in this world, it can only be
 * harvested, not watered.
 */
public class Tomato extends Crop {

    /** Stamina consumed by a successful Tomato harvest. */
    private static final int HARVEST_STAMINA_COST = 2;

    /** Number of Tomatoes produced by a successful harvest. */
    private static final int TOMATOES_PRODUCED = 2;

    /** Increase to the actor's maximum stamina granted by a successful harvest. */
    private static final int MAX_STAMINA_INCREASE = 1;

    /**
     * Creates a new Tomato.
     */
    public Tomato() {
        super('T', "Tomato");
    }

    /**
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
}
