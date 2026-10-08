package game.positions.farm;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TillAction;
import game.actions.WaterAction;
import game.positions.Tillable;
import game.positions.Waterable;

/**
 * A class representing dirt on the ground. Dirt can be tilled into Soil
 * or watered into a Puddle while the Actor is standing on it.
 */
public class Dirt extends Ground implements Tillable, Waterable {

    /**
     * Creates a new Dirt.
     */
    public Dirt() {
        super('.', "Dirt");
    }

    /**
     * Returns the Ground that results from tilling this Dirt.
     *
     * @return a new Soil
     */
    @Override
    public Ground getTillResult() {
        return new Soil();
    }

    /**
     * Returns the Ground that results from watering this Dirt.
     *
     * @return a new Puddle
     */
    @Override
    public Ground getWaterResult() {
        return new Puddle();
    }

    /**
     * Returns the list of Actions available on this Dirt: TillAction and
     * WaterAction, offered only while the Actor is standing on it, not on
     * adjacent tiles.
     *
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of this Dirt from the Actor
     * @return an ActionList containing TillAction and WaterAction if direction is empty,
     * otherwise empty
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (direction.isEmpty()) {
            actions.add(new TillAction(location));
            actions.add(new WaterAction(location));
        }
        return actions;
    }
}
