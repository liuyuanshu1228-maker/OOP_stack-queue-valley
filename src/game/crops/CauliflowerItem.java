package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import game.items.food.ConsumableItem;
import game.random.RandomUtils;

/**
 * A cauliflower, produced by harvesting a {@link Cauliflower} crop. It can
 * be eaten to heal the farmer, and sometimes reproduces itself.
 */
public class CauliflowerItem extends ConsumableItem {

    /** HP restored to the actor when a Cauliflower is eaten. */
    private static final int HEAL_AMOUNT = 25;
    /** Chance (out of 100) that eating a Cauliflower adds another Cauliflower to the inventory. */
    private static final int BONUS_CHANCE_PERCENT = 50;

    /**
     * Creates a new CauliflowerItem.
     */
    public CauliflowerItem() {
        super("Cauliflower", 'k');
    }

    /**
     * Heals the actor, then rolls the chance for another cauliflower to
     * appear in their inventory.
     *
     * @param actor the actor eating this cauliflower
     * @return a description of the actual HP change, accounting for the
     * actor's HP already being at or near its maximum
     */
    @Override
    public String consume(Actor actor) {
        String message = eatToHeal(actor, HEAL_AMOUNT);

        if (RandomUtils.rollPercent(BONUS_CHANCE_PERCENT)) {
            actor.getInventory().add(new CauliflowerItem());
            message += " A bonus cauliflower appears in " + actor + "'s inventory.";
        }

        return message;
    }
}
