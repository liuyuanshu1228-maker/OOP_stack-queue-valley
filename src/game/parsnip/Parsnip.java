package game.parsnip;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.Crop;

/**
 * A parsnip crop. It can be watered, which heals the farmer but risks
 * ruining the plant.
 *
 * @see WaterParsnipAction
 */
public class Parsnip extends Crop {

    /**
     * Constructor.
     */
    public Parsnip() {
        super('p', "Parsnip");
    }

    /**
     * Offers a watering action to an actor standing on or next to this crop.
     *
     * @see Crop#allowableActions(Actor, Location, String)
     * @param actor the actor acting
     * @param location the current location of this crop
     * @param direction the direction of this crop from the actor
     * @return the allowable actions for this crop
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        actions.add(new WaterParsnipAction(this, location));
        return actions;
    }
}
