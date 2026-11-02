package game.positions.area;

import edu.monash.fit2099.engine.positions.Location;

/**
 * A condition that a tile may or may not meet, used to narrow an {@link Area}
 * down (see {@link Area#filter(TileCondition)}).
 * <p>
 * Each way of narrowing an area is its own small class, so a new kind of
 * condition is added by writing a new class, without changing {@link Area}.
 */
public interface TileCondition {

    /**
     * Whether the given tile meets this condition.
     *
     * @param tile the tile to test
     * @return true if the tile meets the condition
     */
    public boolean isMetBy(Location tile);
}
