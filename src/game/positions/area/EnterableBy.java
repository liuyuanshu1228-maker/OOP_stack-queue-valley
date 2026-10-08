package game.positions.area;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Met by the tiles that a given Actor is able to enter.
 */
public class EnterableBy implements TileCondition {

    /** The Actor who would move onto the tile. */
    private final Actor actor;

    /**
     * Creates the condition for an Actor.
     *
     * @param actor the Actor who would move onto the tile
     */
    public EnterableBy(Actor actor) {
        this.actor = actor;
    }

    /**
     * @param tile the tile to test
     * @return true if the actor is able to enter the tile
     */
    @Override
    public boolean isMetBy(Location tile) {
        return tile.canActorEnter(actor);
    }
}
