package game.items.food;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;

/**
 * Abstract base class for portable Items that can be consumed.
 * Subclasses need only define their consume effect via Consumable;
 * the ConsumeAction menu entry is provided automatically.
 */
public abstract class ConsumableItem extends Item implements Consumable {

    /**
     * Constructor.
     *
     * @param name        the name of this Item
     * @param displayChar the character to display for this Item
     */
    public ConsumableItem(String name, char displayChar) {
        super(name, displayChar);
        this.makePortable();
    }

    /**
     * Returns the list of Actions this Item allows its owner to perform,
     * namely consuming it.
     *
     * @param owner the Actor carrying this Item
     * @param map   the map the owner is currently on
     * @return an ActionList containing a ConsumeAction for this Item
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this));
        return actions;
    }
}
