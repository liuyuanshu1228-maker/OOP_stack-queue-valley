package game.positions.area;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Met by the tiles that have an Actor standing on them.
 */
public class HoldsActor implements TileCondition {

    /**
     * @param tile the tile to test
     * @return true if an actor is standing on the tile
     */
    @Override
    public boolean isMetBy(Location tile) {
        return tile.containsAnActor();
    }
}
