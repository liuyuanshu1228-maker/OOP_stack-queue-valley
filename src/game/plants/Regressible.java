package game.plants;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Capability interface for Ground that can be pushed back one growth stage,
 * e.g. by a pest. Callers find it through
 * {@code location.getGroundAs(Regressible.class)}, so they never need to know
 * which plant, or which stage of it, is on the tile.
 * Implementing classes decide what the earlier stage is, including dying back
 * to bare ground when there is no earlier stage.
 */
public interface Regressible {

    /**
     * Replaces this ground with its earlier growth stage at the given Location.
     *
     * @param location the Location this ground currently occupies
     * @return a description of what happened, to be displayed to the player
     */
    String regress(Location location);
}
