package game.positions.area;

import edu.monash.fit2099.engine.positions.Location;
import game.positions.Plantable;

/**
 * Met by the tiles whose ground a plant can be planted on (see
 * {@link Plantable}).
 */
public class PlantableTile implements TileCondition {

    /**
     * Creates a new PlantableTile condition.
     */
    public PlantableTile() {
    }

    /**
     * Checks whether a plant can be planted on the ground of the tile.
     *
     * @param tile the tile to test
     * @return true if the ground on the tile is Plantable
     */
    @Override
    public boolean isMetBy(Location tile) {
        return tile.getGroundAs(Plantable.class) != null;
    }
}
