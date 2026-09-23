package game.strawberry;

import edu.monash.fit2099.engine.actors.Actor;
import game.Chance;
import game.items.ConsumableItem;

/**
 * A strawberry, produced by watering or harvesting a {@link Strawberry}
 * crop. It can be eaten: a rotten strawberry poisons the farmer, while a
 * fresh one heals them over time.
 */
class StrawberryItem extends ConsumableItem {

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
    static StrawberryItem createRandom() {
        return new StrawberryItem(Chance.rollPercent(ROTTEN_CHANCE_PERCENT));
    }

    /**
     * Poisons the actor if this strawberry is rotten, otherwise heals them
     * over time.
     *
     * @param actor the actor eating this strawberry
     */
    @Override
    public void consume(Actor actor) {
        if (rotten) {
            actor.addStatus(new StrawberryPoisonStatus(actor));
            return;
        }

        actor.addStatus(new StrawberryHealStatus(actor));
    }
}
