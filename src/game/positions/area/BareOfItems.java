package game.positions.area;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Met by the tiles that have no Item lying on them.
 */
public class BareOfItems implements TileCondition {

    /**
     * Creates a condition that is met by tiles with no items on them.
     */
    public BareOfItems() {
    }

    /**
     * @param tile the tile to test
     * @return true if no item lies on the tile
     */
    @Override
    public boolean isMetBy(Location tile) {
        return tile.getItems().isEmpty();
    }
}
