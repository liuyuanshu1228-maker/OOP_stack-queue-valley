package game.items;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Capability interface for Items that can be consumed by an Actor.
 * Implementing classes define the effect of being consumed.
 */
public interface Consumable {

    /**
     * Applies the effect of consuming this Item to the given Actor.
     *
     * @param actor the Actor consuming this Item
     * @return a description of what the effect actually did (e.g. the
     * before/after value of whatever it changed), to be displayed to the player
     */
    String consume(Actor actor);
}
