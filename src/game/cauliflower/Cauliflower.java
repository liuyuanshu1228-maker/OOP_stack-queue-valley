package game.cauliflower;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.Crop;

/**
 * A cauliflower crop. Watering it may turn the surrounding ground into
 * puddles, and it can only be harvested once it has been watered enough
 * times.
 *
 * @see WaterCauliflowerAction
 * @see HarvestCauliflowerAction
 */
public class Cauliflower extends Crop {

    /**
     * Constructor.
     */
    public Cauliflower() {
        super('C', "Cauliflower");
    }

    /**
     * Offers watering and harvesting actions to an actor standing on or next
     * to this crop.
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
        actions.add(new WaterCauliflowerAction(this, location));
        actions.add(new HarvestCauliflowerAction(this, location));
        return actions;
    }
}
