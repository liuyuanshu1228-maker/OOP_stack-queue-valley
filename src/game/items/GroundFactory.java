package game.items;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * Creates a new instance of a specific {@link Ground} subclass.
 * <p>
 * Used to inject the concrete Ground type built as the side effect of
 * selling an item (see {@link Stone}), so that the item depends on this
 * interface rather than on any specific Ground subclass.
 */
public interface GroundFactory {

    /**
     * Creates a new instance of the Ground this factory builds.
     *
     * @return a new Ground
     */
    Ground create();
}
