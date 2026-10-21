package game.runes;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * The shape a rune formation can take. A RunePattern does not know or care
 * which Rune is being activated -- it only answers three questions about the
 * geometry around a centre tile: does a formation of this shape exist here,
 * which tiles does that shape reach, and what extra effect (unrelated to any
 * Rune) does completing this particular shape add on top.
 */
public interface RunePattern {

    /**
     * @param center the tile whose rune stone was just placed
     * @return true if rune stones around {@code center} complete this pattern's shape
     */
    boolean matches(Location center);

    /**
     * Only meaningful immediately after {@link #matches(Location)} has
     * returned true for the same {@code center}.
     *
     * @param center the tile whose rune stone was just placed
     * @return the tiles this pattern's shape reaches from {@code center}
     */
    List<Location> affectedTiles(Location center);

    /**
     * Applies whatever extra, Rune-independent effect completing this shape
     * causes (e.g. raising an Altar, planting Thorns). Only meaningful
     * immediately after {@link #matches(Location)} has returned true for the
     * same {@code center}.
     *
     * @param center the tile whose rune stone was just placed
     * @param map the map the formation is on
     * @return a description of the bonus effect, to be displayed to the player
     */
    String applyBonusEffect(Location center, GameMap map);
}
