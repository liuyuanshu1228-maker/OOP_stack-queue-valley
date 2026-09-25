package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import game.Chance;
import game.positions.Cauliflower;

/**
 * A cauliflower, produced by harvesting a {@link Cauliflower} crop. It can
 * be eaten to heal the farmer, and sometimes reproduces itself.
 */
public class CauliflowerItem extends ConsumableItem {

    private static final int HEAL_AMOUNT = 25;
    private static final int BONUS_CHANCE_PERCENT = 50;

    /**
     * Constructor.
     */
    public CauliflowerItem() {
        super("Cauliflower", 'c');
    }

    /**
     * Heals the actor, then rolls the chance for another cauliflower to
     * appear in their inventory.
     *
     * @param actor the actor eating this cauliflower
     */
    @Override
    public void consume(Actor actor) {
        actor.heal(HEAL_AMOUNT);

        if (Chance.rollPercent(BONUS_CHANCE_PERCENT)) {
            actor.getInventory().add(new CauliflowerItem());
        }
    }
}
