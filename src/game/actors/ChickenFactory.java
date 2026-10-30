package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import game.items.ActorFactory;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

/**
 * Creates new {@link Chicken}s named "Chicken".
 * <p>
 * Injected into a {@link game.items.Egg} so that it can spawn a new Chicken
 * when sold, without depending on the Chicken class directly.
 */
public class ChickenFactory implements ActorFactory {

    /** The randomness handed to every Chicken this factory builds. */
    private final RandomUtils randomUtils;

    /** The AreaFactory handed to every Chicken this factory builds. */
    private final AreaFactory areaFactory;

    /**
     * Creates a factory that builds Chickens with the given dependencies.
     *
     * @param randomUtils the randomness handed to every Chicken built
     * @param areaFactory the AreaFactory handed to every Chicken built
     */
    public ChickenFactory(RandomUtils randomUtils, AreaFactory areaFactory) {
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * Creates a new Chicken named "Chicken".
     *
     * @return a new Chicken
     */
    @Override
    public Actor create() {
        return new Chicken("Chicken", randomUtils, areaFactory);
    }
}
