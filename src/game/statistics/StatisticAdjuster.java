package game.statistics;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * Adjusts the statistics of one {@link Actor}.
 * <p>
 * The engine's {@code Actor} cannot be modified, so the statistic rules the game
 * needs on top of it (spending only what the actor has, raising a maximum without
 * restoring the current value) live here, bound to the actor they apply to instead
 * of taking it as a parameter every time. Methods that change a value return a
 * {@link StatisticChange}, which is responsible for describing that change.
 */
public class StatisticAdjuster {

    /** The Actor whose statistics this adjuster changes. */
    private final Actor actor;

    /**
     * Creates a new StatisticAdjuster.
     *
     * @param actor the Actor whose statistics are adjusted
     */
    public StatisticAdjuster(Actor actor) {
        this.actor = actor;
    }

    /**
     * Increases the maximum value of the given statistic without restoring the
     * current value to the new maximum, counteracting BaseStatistic.increaseMaximum()'s
     * default auto-restore behaviour.
     *
     * @param statistic the statistic to modify, e.g. ActorStatistics.STAMINA
     * @param amount the amount by which the maximum increases
     */
    public void increaseMaximumWithoutRestoring(Enum<?> statistic, int amount) {
        int currentValueBeforeMaxIncrease = actor.getStatistic(statistic);
        actor.modifyStatisticMaximum(statistic, StatisticOperations.INCREASE, amount);
        actor.modifyStatistic(statistic, StatisticOperations.UPDATE, currentValueBeforeMaxIncrease);
    }

    /**
     * Checks whether the Actor has at least the given amount of the given statistic.
     *
     * @param statistic the statistic to check, e.g. ActorStatistics.STAMINA
     * @param amount the minimum amount required
     * @return true if the Actor has the statistic and its current value is at least amount
     */
    public boolean hasEnough(Enum<?> statistic, int amount) {
        return actor.hasStatistic(statistic) && actor.getStatistic(statistic) >= amount;
    }

    /**
     * Deducts the given amount of the given statistic, but only if the Actor
     * has enough of it; otherwise leaves the Actor unchanged.
     *
     * @param statistic the statistic to deduct from, e.g. ActorStatistics.STAMINA
     * @param amount the amount to deduct
     * @return true if the amount was deducted, false if the Actor did not have enough
     */
    public boolean trySpend(Enum<?> statistic, int amount) {
        if (!hasEnough(statistic, amount)) {
            return false;
        }
        actor.modifyStatistic(statistic, StatisticOperations.DECREASE, amount);
        return true;
    }

    /**
     * Increases the current value of the given statistic, capped at its maximum.
     *
     * @param statistic the statistic to increase, e.g. ActorStatistics.HEALTH
     * @param amount the requested increase
     * @return the actual change in the statistic's current value
     */
    public StatisticChange increase(Enum<?> statistic, int amount) {
        int before = actor.getStatistic(statistic);
        actor.modifyStatistic(statistic, StatisticOperations.INCREASE, amount);
        return new StatisticChange(before, actor.getStatistic(statistic));
    }

    /**
     * Increases the maximum value of the given statistic, which also restores
     * the current value to the new maximum.
     *
     * @param statistic the statistic to modify, e.g. ActorStatistics.HEALTH
     * @param amount the amount by which the maximum increases
     * @return the change in the statistic's maximum value
     */
    public StatisticChange increaseMaximum(Enum<?> statistic, int amount) {
        int before = actor.getMaximumStatistic(statistic);
        actor.modifyStatisticMaximum(statistic, StatisticOperations.INCREASE, amount);
        return new StatisticChange(before, actor.getMaximumStatistic(statistic));
    }
}
