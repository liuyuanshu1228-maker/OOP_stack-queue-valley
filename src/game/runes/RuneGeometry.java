package game.runes;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Small geometry helpers shared by the RunePattern implementations: stepping
 * to a tile at a relative offset (bounds-checked), and asking whether a tile
 * carries a rune stone. Kept in one place so that each pattern's own code is
 * about its shape, not about map bounds or how a rune stone is detected --
 * three separate pattern classes doing their own bounds-checking would be
 * the same few lines copied three times (DRY).
 * <p>
 * Package-private: this is an implementation detail of the rune patterns in
 * this package, not part of the Rune/RunePattern API other code should
 * depend on.
 */
final class RuneGeometry {

    private RuneGeometry() {
        // Utility class; not instantiable.
    }

    /**
     * @param base the tile to step from
     * @param dx how far east (positive) or west (negative) to step
     * @param dy how far south (positive) or north (negative) to step
     * @return the tile at that offset from {@code base}, or null if it would fall off the map
     */
    static Location offsetOrNull(Location base, int dx, int dy) {
        GameMap map = base.map();
        int x = base.x() + dx;
        int y = base.y() + dy;
        if (!map.getXRange().contains(x) || !map.getYRange().contains(y)) {
            return null;
        }
        return map.at(x, y);
    }

    /**
     * @param tile the tile to check, or null
     * @return true if {@code tile} is not null and its ground carries a Rune
     */
    static boolean hasRuneStone(Location tile) {
        return tile != null && tile.getGroundAs(CarriesRune.class) != null;
    }
}
