package game.positions.area;

import edu.monash.fit2099.engine.positions.Location;
import game.positions.Waterable;

/**
 * Met by the tiles whose Ground exposes the {@link Waterable} capability.
 */
public class WaterableTile implements TileCondition {

    /**
     * @param tile the tile to test
     * @return true if the tile's Ground can be watered
     */
    @Override
    public boolean isMetBy(Location tile) {
        return tile.getGroundAs(Waterable.class) != null;
    }
}
