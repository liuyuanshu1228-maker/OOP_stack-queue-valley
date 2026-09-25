package game.positions;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BreakAction;

/**
 * Abstract base class for Ground that cannot be entered and must be broken to
 * clear the way. Breaking it always leaves dirt behind and drops an item on its
 * tile; subclasses only say which item drops and what else happens.
 */
public abstract class BreakableGround extends Ground implements Breakable {

    /**
     * Constructor.
     *
     * @param displayChar character to display for this ground
     * @param name the name of this ground
     */
    public BreakableGround(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * Breakable ground can never be entered.
     *
     * @param actor the Actor to check
     * @return false
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Offers a breaking action to an Actor standing next to this ground.
     *
     * @param actor the Actor acting
     * @param location the current Location of this ground
     * @param direction the direction of this ground from the Actor
     * @return an ActionList containing a BreakAction
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        actions.add(new BreakAction(this, location, direction));
        return actions;
    }

    /**
     * Turns this ground into dirt, drops its item on the tile, then applies the
     * effect specific to the kind of ground.
     *
     * @param actor the Actor breaking this ground
     * @param location the Location of this ground
     * @return a description of the outcome
     */
    @Override
    public final String breakUp(Actor actor, Location location) {
        location.setGround(new Dirt());
        location.addItem(createDrop());
        return actor + " breaks the " + this + ". " + afterBreaking(actor, location);
    }

    /**
     * @return a new instance of the item dropped when this ground is broken
     */
    abstract Item createDrop();

    /**
     * Applies the effect specific to this kind of ground. Called once the
     * ground has already been turned into dirt.
     *
     * @param actor the Actor who broke this ground
     * @param location the Location this ground occupied
     * @return a description of what happened
     */
    abstract String afterBreaking(Actor actor, Location location);
}