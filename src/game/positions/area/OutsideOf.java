package game.positions.area;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Met by the tiles that are not part of a given Area.
 */
public class OutsideOf implements TileCondition {

    /** The area whose tiles do not meet the condition. */
    private final Area excluded;

    /**
     * Creates the condition for an Area.
     *
     * @param excluded the area whose tiles do not meet the condition
     */
    public OutsideOf(Area excluded) {
        this.excluded = excluded;
    }

    /**
     * @param tile the tile to test
     * @return true if the tile is not one of the excluded area's tiles
     */
    @Override
    public boolean isMetBy(Location tile) {
        return !excluded.tiles().contains(tile);
    }
}
