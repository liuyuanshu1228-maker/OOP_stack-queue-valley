package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * Utility for adjusting an actor's statistics.
 */
public final class Stats {

    private Stats() {
    }

    /**
     * Increases the maximum of a statistic without also restoring its
     * current value to the new maximum.
     * <p>
     * {@code BaseStatistic.increaseMaximum()} always sets the current value
     * of the statistic to the new maximum as a side effect. This method
     * works around that so that only the ceiling moves and the current
     * value is left exactly as it was.
     *
     * @param actor the actor whose statistic is being increased
     * @param statistic the statistic to increase the maximum of
     * @param amount the amount to increase the maximum by
     */
    public static <E extends Enum<E>> void increaseMaximumWithoutRestoring(Actor actor, Enum<E> statistic, int amount) {
        int currentValue = actor.getStatistic(statistic);
        actor.modifyStatisticMaximum(statistic, StatisticOperations.INCREASE, amount);
        actor.modifyStatistic(statistic, StatisticOperations.UPDATE, currentValue);
    }
}
