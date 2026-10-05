package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import game.capabilities.StrawberryHealStatus;
import game.capabilities.StrawberryPoisonStatus;
import game.items.food.ConsumableItem;
import game.RandomUtils;

/**
 * A strawberry, produced by watering or harvesting a {@link Strawberry}
 * crop. It can be eaten: a rotten strawberry poisons the farmer, while a
 * fresh one heals them over time.
 */
public class StrawberryItem extends ConsumableItem {

    private static final int ROTTEN_CHANCE_PERCENT = 50;

    private final boolean rotten;

    /**
     * Constructor.
     *
     * @param rotten whether this strawberry is rotten
     */
    StrawberryItem(boolean rotten) {
        super(rotten ? "Rotten Strawberry" : "Strawberry", 's');
        this.rotten = rotten;
    }

    /**
     * Creates a strawberry that has a {@value #ROTTEN_CHANCE_PERCENT}%
     * chance of being rotten.
     *
     * @return a new, randomly rotten or fresh strawberry
     */
    public static StrawberryItem createRandom() {
        return new StrawberryItem(RandomUtils.rollPercent(ROTTEN_CHANCE_PERCENT));
    }

    /**
     * Poisons the actor if this strawberry is rotten, otherwise heals them
     * over time.
     *
     * @param actor the actor eating this strawberry
     * @return a description of the effect that was applied
     */
    @Override
    public String consume(Actor actor) {
        if (rotten) {
            actor.addStatus(new StrawberryPoisonStatus(actor));
            return actor + " eats the rotten strawberry and is poisoned for "
                    + StrawberryPoisonStatus.DAMAGE_PER_TURN + " damage per turn over "
                    + StrawberryPoisonStatus.DURATION_TURNS + " turns.";
        }

        actor.addStatus(new StrawberryHealStatus(actor));
        return actor + " eats the strawberry and is healed for "
                + StrawberryHealStatus.HEAL_PER_TURN + " HP per turn over "
                + StrawberryHealStatus.DURATION_TURNS + " turns.";
    }
}
