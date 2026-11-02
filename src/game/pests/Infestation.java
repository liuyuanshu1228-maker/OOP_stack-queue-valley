package game.pests;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Something that damages the plant it stands on, such as a pest.
 * <p>
 * Defences find pests through this interface (see {@link PestPresent}), so
 * they never depend on a concrete pest, and a new pest needs no change to any
 * defence.
 */
public interface Infestation {

    /**
     * Damages the plant on the given tile, together with anything else this
     * pest affects around it.
     *
     * @param here the tile this pest stands on
     * @return a description of what happened, or an empty string if nothing
     * happened
     */
    public String feedOn(Location here);
}
