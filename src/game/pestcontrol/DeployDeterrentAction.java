package game.pestcontrol;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Uses a pest deterrent where the actor stands, and uses up the item it came
 * from.
 * <p>
 * It depends only on {@link PestDeterrent}, so a new kind of defence needs no
 * change here. The item is kept separately because the actor's inventory
 * holds items, not deterrents.
 */
public class DeployDeterrentAction extends Action {

    /** The item that is used up. */
    private final Item source;

    /** The defence that acts on the pests. */
    private final PestDeterrent deterrent;

    /**
     * Creates an action that uses a deterrent.
     *
     * @param source the item to remove from the actor's inventory
     * @param deterrent the defence to use
     */
    public DeployDeterrentAction(Item source, PestDeterrent deterrent) {
        this.source = source;
        this.deterrent = deterrent;
    }

    /**
     * Uses the deterrent where the actor stands and removes the item from the
     * actor's inventory.
     *
     * @param actor the actor using the deterrent
     * @param map the map containing the actor
     * @return what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        actor.getInventory().remove(source);
        return actor + " uses the " + source + ". " + deterrent.deterAround(map.locationOf(actor), map);
    }

    /**
     * @param actor the actor using the deterrent
     * @return the menu text for this action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses the " + source;
    }
}
