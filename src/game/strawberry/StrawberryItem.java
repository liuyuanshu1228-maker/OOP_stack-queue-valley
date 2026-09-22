package game.strawberry;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.Random;

/**
 * A strawberry, produced by watering or harvesting a {@link Strawberry}
 * crop. It can be eaten: a rotten strawberry poisons the farmer, while a
 * fresh one heals them over time.
 */
class StrawberryItem extends Item {

    private static final int ROTTEN_CHANCE_PERCENT = 50;
    private static final Random RANDOM = new Random();

    private final boolean rotten;

    /**
     * Constructor.
     *
     * @param rotten whether this strawberry is rotten
     */
    StrawberryItem(boolean rotten) {
        super(rotten ? "Rotten Strawberry" : "Strawberry", 's');
        this.rotten = rotten;
        makePortable();
    }

    /**
     * Creates a strawberry that has a {@value #ROTTEN_CHANCE_PERCENT}%
     * chance of being rotten.
     *
     * @return a new, randomly rotten or fresh strawberry
     */
    static StrawberryItem createRandom() {
        return new StrawberryItem(RANDOM.nextInt(100) < ROTTEN_CHANCE_PERCENT);
    }

    /**
     * @return true if this strawberry is rotten
     */
    boolean isRotten() {
        return rotten;
    }

    /**
     * Offers an action to eat this strawberry to whichever actor is
     * carrying it.
     *
     * @see Item#allowableActions(Actor, GameMap)
     * @param owner the actor carrying this strawberry
     * @param map the map the actor is on
     * @return the allowable actions for this strawberry
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);
        actions.add(new ConsumeStrawberryAction(this));
        return actions;
    }
}
