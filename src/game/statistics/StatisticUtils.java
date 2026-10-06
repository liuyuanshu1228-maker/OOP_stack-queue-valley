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

    /**
     * Deducts the given amount of the given statistic, but only if the Actor
     * has enough of it; otherwise leaves the Actor unchanged.
     *
     * @param actor     the Actor paying the cost
     * @param statistic the statistic to deduct from, e.g. ActorStatistics.STAMINA
     * @param amount    the amount to deduct
     * @return true if the amount was deducted, false if the Actor did not have enough
     */
    public static boolean trySpend(Actor actor, Enum<?> statistic, int amount) {
        if (!hasEnough(actor, statistic, amount)) {
            return false;
        }
        actor.modifyStatistic(statistic, StatisticOperations.DECREASE, amount);
        return true;
    }

    /**
     * Increases the current value of the given statistic, capped at its maximum,
     * and describes the actual change, e.g. "5 HP (80 -> 85)".
     *
     * @param actor     the Actor whose statistic is being increased
     * @param statistic the statistic to increase, e.g. ActorStatistics.HEALTH
     * @param amount    the requested increase
     * @param unit      the unit shown after the amount, e.g. "HP" or "stamina"
     * @return a description of the actual change in the statistic's current value
     */
    public static String increaseAndDescribe(Actor actor, Enum<?> statistic, int amount, String unit) {
        int before = actor.getStatistic(statistic);
        actor.modifyStatistic(statistic, StatisticOperations.INCREASE, amount);
        return describeChange(before, actor.getStatistic(statistic), unit);
    }

    /**
     * Increases the maximum value of the given statistic, which also restores
     * the current value to the new maximum, and describes the change in the
     * maximum, e.g. "2 (10 -> 12)".
     *
     * @param actor     the Actor whose statistic is being modified
     * @param statistic the statistic to modify, e.g. ActorStatistics.HEALTH
     * @param amount    the amount by which the maximum increases
     * @return a description of the change in the statistic's maximum value
     */
    public static String increaseMaximumAndDescribe(Actor actor, Enum<?> statistic, int amount) {
        int before = actor.getMaximumStatistic(statistic);
        actor.modifyStatisticMaximum(statistic, StatisticOperations.INCREASE, amount);
        return describeChange(before, actor.getMaximumStatistic(statistic), "");
    }

    /**
     * Formats a change in a statistic as "amount unit (before -> after)".
     *
     * @param before the value before the change
     * @param after  the value after the change
     * @param unit   the unit shown after the amount, or an empty string for none
     * @return the formatted description
     */
    private static String describeChange(int before, int after, String unit) {
        String amountText = unit.isEmpty() ? String.valueOf(after - before) : (after - before) + " " + unit;
        return amountText + " (" + before + " -> " + after + ")";
    }
}
