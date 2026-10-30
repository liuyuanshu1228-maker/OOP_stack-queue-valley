package game.actions;

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

    /**
     * Tells whether the given Actor has what the effect of this Item needs.
     * An effect that changes a statistic only works on an Actor that has that
     * statistic, so an Actor without it must not consume the Item. Items whose
     * effect needs nothing special keep the default, which allows every Actor.
     *
     * @param actor the Actor that would consume this Item
     * @return true if {@link #consume(Actor)} can be called with this Actor
     */
    public default boolean canBeConsumedBy(Actor actor) {
        return true;
    }
}
