package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.Consumable;

import java.util.Optional;

/**
 * An Action that allows an Actor to consume a portable Item that implements Consumable.
 * Applies the Item's consume effect, removes the Item from the Actor's inventory, and ends the Actor's turn.
 */
public class ConsumeAction extends Action {

    private final Item item;

    /**
     * Constructor.
     *
     * @param item the Item to be consumed
     */
    public ConsumeAction(Item item) {
        this.item = item;
    }

    /**
     * Applies the Item's Consumable effect and removes it from the Actor's inventory.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Optional<Consumable> consumable = item.asCapability(Consumable.class);

        if (consumable.isEmpty()) {
            return actor + " cannot consume " + item + ".";
        }

        String message = consumable.get().consume(actor);
        actor.getInventory().remove(item);
        return message;
    }

    /**
     * Describes what this Action will do if chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " consumes the " + item + ".";
    }
}
