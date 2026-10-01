package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.positions.Melon;

/**
 * A melon, produced by harvesting a {@link Melon} crop. It can be eaten to
 * permanently raise the farmer's maximum HP, restoring current HP to the
 * new maximum.
 */
public class MelonItem extends ConsumableItem {

    private static final int MAX_HEALTH_INCREASE_AMOUNT = 10;

    /**
     * Constructor.
     */
    public MelonItem() {
        super("Melon", 'm');
    }

    /**
     * Raises the actor's maximum HP, restoring their current HP to the new
     * maximum.
     *
     * @param actor the actor eating this melon
     * @return a description of the actual maximum HP change and the
     * resulting current HP
     */
    @Override
    public String consume(Actor actor) {
        int maxHealthBefore = actor.getMaximumStatistic(ActorStatistics.HEALTH);
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, MAX_HEALTH_INCREASE_AMOUNT);
        int maxHealthAfter = actor.getMaximumStatistic(ActorStatistics.HEALTH);
        int healthAfter = actor.getStatistic(ActorStatistics.HEALTH);

        return actor + " eats the melon. Maximum HP increases by " + (maxHealthAfter - maxHealthBefore)
                + " (" + maxHealthBefore + " -> " + maxHealthAfter + "), and current HP is restored to " + healthAfter + ".";
    }
}
