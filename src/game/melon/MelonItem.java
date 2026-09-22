package game.melon;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import game.ConsumableItem;
import game.Stats;

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
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    protected String consume(Actor actor, GameMap map) {
        Stats.increaseMaximumWithoutRestoring(actor, ActorStatistics.HEALTH, MAX_HEALTH_INCREASE_AMOUNT);
        return actor + " eats the melon. Their maximum HP increases by " + MAX_HEALTH_INCREASE_AMOUNT + ".";
    }
}
