package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import game.items.food.ConsumableItem;
import game.random.RandomUtils;

/**
 * A strawberry, produced by watering or harvesting a {@link Strawberry}
 * crop. It can be eaten: a rotten strawberry poisons the farmer, while a
 * fresh one heals them over time.
 */
public class StrawberryItem extends ConsumableItem {

    /** Chance (out of 100) that a newly created Strawberry is rotten. */
    private static final int ROTTEN_CHANCE_PERCENT = 50;

    private final boolean rotten;

    /**
     * Creates a new StrawberryItem that has a {@value #ROTTEN_CHANCE_PERCENT}%
     * chance of being rotten.
     *
     * @param randomUtils the randomness used to roll the rotten chance
     */
    public StrawberryItem(RandomUtils randomUtils) {
        this(randomUtils.rollPercent(ROTTEN_CHANCE_PERCENT));
    }

    /**
     * Creates a new StrawberryItem with a known rotten/fresh outcome.
     *
     * @param rotten whether this strawberry is rotten
     */
    private StrawberryItem(boolean rotten) {
        super(rotten ? "Rotten Strawberry" : "Strawberry", 's');
        this.rotten = rotten;
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
            StrawberryPoisonStatus status = new StrawberryPoisonStatus(actor);
            actor.addStatus(status);
            return actor + " eats the rotten strawberry and is " + status.describeEffect() + ".";
        }

        StrawberryHealStatus status = new StrawberryHealStatus(actor);
        actor.addStatus(status);
        return actor + " eats the strawberry and is " + status.describeEffect() + ".";
    }
}
