package game.melon;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import game.StatisticUtils;
import game.items.ConsumableItem;

/**
 * A melon, produced by harvesting a {@link Melon} crop. It can be eaten to
 * permanently raise the farmer's maximum HP.
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
     * Raises the actor's maximum HP, without topping up their current HP to
     * match.
     *
     * @param actor the actor eating this melon
     */
    @Override
    public void consume(Actor actor) {
        StatisticUtils.increaseMaximumWithoutRestoring(actor, ActorStatistics.HEALTH, MAX_HEALTH_INCREASE_AMOUNT);
    }
}
