package game.crops;

import edu.monash.fit2099.engine.positions.Location;
import game.positions.area.TileCondition;

/**
 * Met by the tiles where no crop is growing.
 * <p>
 * It lives in the crops package because "what counts as a crop" is knowledge
 * of that package, which the area package must not depend on.
 */
public class Unplanted implements TileCondition {

    /**
     * @param tile the tile to test
     * @return true if the ground on the tile is not a crop
     */
    @Override
    public boolean isMetBy(Location tile) {
        return tile.getGroundAs(Crop.class) == null;
    }
}
