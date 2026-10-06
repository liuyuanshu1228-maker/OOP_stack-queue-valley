package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.tools.TillingTool;
import game.positions.Tillable;

/**
 * An Action that allows an Actor to till the Ground at their current Location,
 * provided they are carrying a Hoe and have enough stamina.
 * The Ground must implement Tillable to define what it becomes once tilled.
 */
public class TillAction extends CapabilityToolAction<Tillable, TillingTool> {

    /**
     * Constructor.
     *
     * @param location the Location whose Ground is to be tilled
     */
    public TillAction(Location location) {
        super(location, Tillable.class, TillingTool.class,
                "has no hoe to till the ground.", "cannot till this ground.");
    }

    /**
     * Tills the Ground, replacing it with the Tillable's till result.
     *
     * @param actor the actor performing the action
     * @param location the Location whose Ground is to be tilled
     * @param tillable the Tillable capability of the Ground
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    protected String applyEffect(Actor actor, Location location, Tillable tillable) {
        location.setGround(tillable.getTillResult());
        return actor + " tills the ground.";
    }

    /**
     * Describes what this Action will do if chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " tills the ground here.";
    }
}