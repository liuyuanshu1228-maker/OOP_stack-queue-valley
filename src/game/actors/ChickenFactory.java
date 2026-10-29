package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import game.items.ActorFactory;

/**
 * Creates new {@link Chicken}s named "Chicken".
 * <p>
 * Injected into a {@link game.items.Egg} so that it can spawn a new Chicken
 * when sold, without depending on the Chicken class directly.
 */
public class ChickenFactory implements ActorFactory {

    /**
     * Creates a new Chicken named "Chicken".
     *
     * @return a new Chicken
     */
    @Override
    public Actor create() {
        return new Chicken("Chicken");
    }
}
