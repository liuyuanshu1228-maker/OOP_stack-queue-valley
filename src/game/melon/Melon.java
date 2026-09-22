package game.melon;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.Crop;

/**
 * A melon crop. Watering it may raise the farmer's maximum stamina, and it
 * can be harvested at any time, though harvesting risks an explosion that
 * damages everyone nearby.
 *
 * @see WaterMelonAction
 * @see HarvestMelonAction
 */
public class Melon extends Crop {

    /**
     * Constructor.
     */
    public Melon() {
        super('M', "Melon");
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
        actions.add(new WaterMelonAction(this, location));
        actions.add(new HarvestMelonAction(this, location));
        return actions;
    }
}
