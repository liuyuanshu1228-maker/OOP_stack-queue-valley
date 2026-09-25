package game.statistics;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * Utility methods for modifying an Actor's statistics.
 */
public final class StatisticUtils {

    private StatisticUtils() {
        // Prevent instantiation; this class only provides static utility methods.
    }

    /**
     * Increases the maximum value of the given statistic without restoring the
     * current value to the new maximum, counteracting BaseStatistic.increaseMaximum()'s
     * default auto-restore behaviour.
     *
     * @param actor     the Actor whose statistic is being modified
     * @param statistic the statistic to modify, e.g. ActorStatistics.STAMINA
     * @param amount    the amount by which the maximum increases
     */
    public static void increaseMaximumWithoutRestoring(Actor actor, Enum<?> statistic, int amount) {
        int currentValueBeforeMaxIncrease = actor.getStatistic(statistic);
        actor.modifyStatisticMaximum(statistic, StatisticOperations.INCREASE, amount);
        actor.modifyStatistic(statistic, StatisticOperations.UPDATE, currentValueBeforeMaxIncrease);
    }

    /**
     * Checks whether the given Actor has at least the given amount of the given statistic.
     *
     * @param actor     the Actor to check
     * @param statistic the statistic to check, e.g. ActorStatistics.STAMINA
     * @param amount    the minimum amount required
     * @return true if the Actor has the statistic and its current value is at least amount
     */
    public static boolean hasEnough(Actor actor, Enum<?> statistic, int amount) {
        return actor.hasStatistic(statistic) && actor.getStatistic(statistic) >= amount;
    }
}
