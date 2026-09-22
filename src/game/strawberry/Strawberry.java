package game.strawberry;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.Crop;

/**
 * A strawberry crop. It can be watered, which scatters strawberries onto
 * adjacent tiles, and it can be harvested at any time for strawberries,
 * though harvesting risks destroying the plant.
 *
 * @see WaterStrawberryAction
 * @see HarvestStrawberryAction
 */
public class Strawberry extends Crop {

    /**
     * Constructor.
     */
    public Strawberry() {
        super('S', "Strawberry");
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
        actions.add(new WaterStrawberryAction(this, location));
        actions.add(new HarvestStrawberryAction(this, location));
        return actions;
    }
}
