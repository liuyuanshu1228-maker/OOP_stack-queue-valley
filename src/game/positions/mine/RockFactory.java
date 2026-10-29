package game.positions.mine;

import edu.monash.fit2099.engine.positions.Ground;
import game.items.GroundFactory;

/**
 * Creates new {@link Rock}s.
 * <p>
 * Injected into a {@link game.items.Stone} so that it can build a new Rock
 * when sold, without depending on the Rock class directly.
 */
public class RockFactory implements GroundFactory {

    /**
     * Creates a new Rock.
     *
     * @return a new Rock
     */
    @Override
    public Ground create() {
        return new Rock();
    }
}
