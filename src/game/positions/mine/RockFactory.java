package game.positions.mine;

import edu.monash.fit2099.engine.positions.Ground;
import game.items.GroundFactory;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

/**
 * Creates new {@link Rock}s.
 * <p>
 * Injected into a {@link game.items.Stone} so that it can build a new Rock
 * when sold, without depending on the Rock class directly. Holds the
 * dependencies a new Rock needs, purely to forward them on construction.
 */
public class RockFactory implements GroundFactory {

    /** Forwarded to each Rock this factory creates. */
    private final RandomUtils randomUtils;

    /** Forwarded to each Rock this factory creates. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new RockFactory.
     *
     * @param randomUtils forwarded to each Rock this factory creates
     * @param areaFactory forwarded to each Rock this factory creates
     */
    public RockFactory(RandomUtils randomUtils, AreaFactory areaFactory) {
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * Creates a new Rock.
     *
     * @return a new Rock
     */
    @Override
    public Ground create() {
        return new Rock(randomUtils, areaFactory);
    }
}
