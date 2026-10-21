package game.runes;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * The effect a rune carries. A Rune does not know or care what shape of
 * formation triggered it -- it is only told which tiles to affect and who
 * triggered it. The shape (how many tiles, in what pattern) is a
 * {@link RunePattern}'s concern, not the Rune's: this split is what lets any
 * Rune combine with any RunePattern without either one knowing the other
 * exists.
 */
public interface Rune {

    /**
     * Applies this Rune's effect to every tile in {@code affectedTiles}.
     *
     * @param affectedTiles the tiles the activated formation reaches
     * @param activator the Actor who placed the rune stone that triggered this activation
     * @return a description of what happened, to be displayed to the player
     */
    String applyTo(List<Location> affectedTiles, Actor activator);
}
