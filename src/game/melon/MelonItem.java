package game.melon;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.items.ConsumableItem;

/**
 * A melon, produced by harvesting a {@link Melon} crop. It can be eaten to
 * permanently raise the farmer's maximum HP, restoring current HP to the
 * new maximum.
 */
class MelonItem extends ConsumableItem {

    private static final int MAX_HEALTH_INCREASE_AMOUNT = 10;

    /**
     * Constructor.
     */
    MelonItem() {
        super("Melon", 'm');
    }

    /**
     * Raises the actor's maximum HP, restoring their current HP to the new
     * maximum.
     *
     * @param actor the actor eating this melon
     */
    @Override
    public void consume(Actor actor) {
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, MAX_HEALTH_INCREASE_AMOUNT);
    }
}
