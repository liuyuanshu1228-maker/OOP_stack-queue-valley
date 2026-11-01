package game.pestcontrol;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An item the Farmer carries and uses once to defend the farm against pests.
 * <p>
 * While carried, it offers a {@link DeployDeterrentAction}. A subclass only
 * says what using it does ({@link #deterAround}) and, if it needs to, where
 * it can be used ({@link #canDeployAt}).
 */
public abstract class DeterrentItem extends Item implements PestDeterrent {

    /**
     * Creates a deterrent item.
     *
     * @param name the name of the item
     * @param displayChar the character shown on the map
     */
    protected DeterrentItem(String name, char displayChar) {
        super(name, displayChar);
    }

    /**
     * Offers to use this item where the owner stands, if it can be used there.
     *
     * @param owner the actor carrying this item
     * @param map the map containing the owner
     * @return a list holding a DeployDeterrentAction, or an empty list
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (canDeployAt(map.locationOf(owner))) {
            actions.add(new DeployDeterrentAction(this, this));
        }
        return actions;
    }

    /**
     * Decides whether this item can be used on the given tile. By default it
     * can be used anywhere.
     *
     * @param here the tile the owner stands on
     * @return true if this item can be used there
     */
    protected boolean canDeployAt(Location here) {
        return true;
    }
}
