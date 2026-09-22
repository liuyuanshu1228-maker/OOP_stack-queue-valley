package game;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * The action of eating a {@link ConsumableItem}.
 */
class ConsumeAction extends Action {

    private final ConsumableItem item;

    /**
     * Constructor.
     *
     * @param item the item to be eaten
     */
    ConsumeAction(ConsumableItem item) {
        this.item = item;
    }

    /**
     * Removes the item from the actor's inventory, then applies its effect.
     *
     * @see Action#execute(Actor, GameMap)
     * @param actor the actor eating the item
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        actor.getInventory().remove(item);
        return item.consume(actor, map);
    }

    /**
     * Describes this action in a format suitable for displaying in the menu.
     *
     * @see Action#menuDescription(Actor)
     * @param actor the actor performing the action
     * @return a string, e.g. "Farmer Bob eats the Cauliflower"
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " eats the " + item;
    }
}
