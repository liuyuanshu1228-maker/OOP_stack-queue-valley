package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Capability interface for Actors (animals) that can be fed by the Farmer.
 * Implementing classes define their own heal amount and the item(s)
 * dropped near the Farmer as a result of being fed.
 */
public interface Feedable {

    /**
     * Applies this animal's heal effect and produces its animal-specific
     * item(s) on tile(s) adjacent to the Farmer's location.
     *
     * @param farmer the Actor doing the feeding
     * @param farmerLocation the Location of the Farmer (where drops appear nearby)
     * @return a description of the outcome, to be displayed to the player
     */
    String feed(Actor farmer, Location farmerLocation);
}
