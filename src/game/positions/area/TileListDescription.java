package game.positions.area;

import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Describes a list of tiles as a single string, so a message about an effect
 * that touches several tiles at once still names exactly where each one
 * happened. Each tile already prints its own coordinates through
 * {@link Location#toString()}, so this class only joins them.
 * <p>
 * Pulled out on its own rather than repeated inside every Enchantment and
 * every Enchantable tool, which would otherwise all format the same kind of
 * list the same way.
 */
public class TileListDescription {

    /** Printed when the list of tiles is empty. */
    private static final String NO_TILES = "nowhere";

    /** Text used to join two tile descriptions. */
    private static final String SEPARATOR = ", ";

    /**
     * @param tiles the tiles to describe
     * @return the tiles joined together, or "nowhere" if the list is empty
     */
    public String of(List<Location> tiles) {
        if (tiles.isEmpty()) {
            return NO_TILES;
        }
        StringBuilder description = new StringBuilder();
        for (Location tile : tiles) {
            if (description.length() > 0) {
                description.append(SEPARATOR);
            }
            description.append(tile);
        }
        return description.toString();
    }
}
