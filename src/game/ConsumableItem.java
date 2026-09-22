package game;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Abstract base class for items that can be eaten.
 * <p>
 * Offers an action to eat this item to whichever actor is carrying it;
 * eating removes the item from the actor's inventory and applies its
 * item-specific effect.
 */
public abstract class ConsumableItem extends Item {

    /**
     * Constructor. A consumable item is portable, so it can be carried
     * until it is eaten.
     *
     * @param name the name of this item
     * @param displayChar the character to use to represent this item if it is on the ground
     */
    protected ConsumableItem(String name, char displayChar) {
        super(name, displayChar);
        makePortable();
    }

    /**
     * Applies the effect of eating this item. Called only after the item
     * has already been removed from the actor's inventory.
     *
     * @param actor the actor eating this item
     * @param map the map the actor is on
     * @return a description of what happened
     */
    protected abstract String consume(Actor actor, GameMap map);

    /**
     * Offers an action to eat this item to whichever actor is carrying it.
     *
     * @see Item#allowableActions(Actor, GameMap)
     * @param owner the actor carrying this item
     * @param map the map the actor is on
     * @return the allowable actions for this item
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);
        actions.add(new ConsumeAction(this));
        return actions;
    }
}
