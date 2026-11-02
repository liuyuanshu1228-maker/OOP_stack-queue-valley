package game.actions;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Capability interface for Items that can be used as food to feed an animal.
 * Implementing classes define the bonus effect applied to the Farmer,
 * independent of which animal was fed.
 */
public interface Food {

    /**
     * Applies this food's bonus effect to the Actor who used it for feeding.
     *
     * @param farmer the Actor who fed an animal using this food
     * @return a description of the bonus, to be shown to the player
     */
    public String applyFeedingBonus(Actor farmer);
}
