package game.positions.area;

import edu.monash.fit2099.engine.positions.Location;
import game.positions.Breakable;

/**
 * Met by the tiles whose Ground exposes the {@link Breakable} capability.
 */
public class BreakableTile implements TileCondition {

    /**
     * @param tile the tile to test
     * @return true if the tile's Ground can be broken
     */
    @Override
    public boolean isMetBy(Location tile) {
        return tile.getGroundAs(Breakable.class) != null;
    }
}
