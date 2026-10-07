package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import game.items.food.ConsumableItem;

/**
 * A melon, produced by harvesting a {@link Melon} crop. It can be eaten to
 * permanently raise the farmer's maximum HP, restoring current HP to the
 * new maximum.
 */
public class MelonItem extends ConsumableItem {

    /** Amount by which eating a Melon raises the actor's maximum HP. */
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
        return eatToRaiseMaxHealth(actor, MAX_HEALTH_INCREASE_AMOUNT);
    }
}
