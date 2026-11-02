package game.items;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Creates a new instance of a specific {@link Actor} subclass.
 * <p>
 * Used to inject the concrete Actor type spawned as the side effect of
 * selling an item (see {@link Egg}), so that the item depends on this
 * interface rather than on any specific Actor subclass.
 */
public interface ActorFactory {

    /**
     * Creates a new instance of the Actor this factory builds.
     *
     * @return a new Actor
     */
    Actor create();
}
