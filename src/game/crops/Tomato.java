package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import game.plants.Produce;
import game.plants.SelfReplicate;
import game.statistics.StatisticAdjuster;

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
public class Tomato extends Crop {

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

    /**
     * Creates a new Tomato.
     */
    public Tomato() {
        super('T', "Tomato",
                new Produce(PRODUCE_INTERVAL, TomatoItem::new),
                new SelfReplicate(SPREAD_INTERVAL, SPREAD_CHANCE_PERCENT, Tomato::new));
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
}
