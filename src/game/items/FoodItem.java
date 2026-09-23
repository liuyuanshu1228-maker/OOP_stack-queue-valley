package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.FeedAction;
import game.actors.Feedable;

/**
 * Abstract base class for portable Items that can be consumed, used as animal food,
 * and processed by a ProcessingFacility.
 * Subclasses need only define their consume() effect and applyFeedingBonus() effect;
 * ConsumeAction, FeedAction (when adjacent to a Feedable animal), and processing support
 * are all provided automatically.
 */
public abstract class FoodItem extends ConsumableItem implements Food, Processable {

    /**
     * Constructor.
     *
     * @param name        the name of this Item
     * @param displayChar the character to display for this Item
     */
    public FoodItem(String name, char displayChar) {
        super(name, displayChar);
    }

    /**
     * Returns the list of Actions this food Item allows its owner to perform on
     * another Actor, namely a FeedAction if that Actor is Feedable.
     *
     * @param otherActor the other Actor
     * @param location   the location of the other Actor
     * @return an ActionList containing a FeedAction if otherActor is Feedable, otherwise empty
     */
    @Override
    public ActionList allowableActions(Actor otherActor, Location location) {
        ActionList actions = new ActionList();
        if (otherActor.asCapability(Feedable.class).isPresent()) {
            actions.add(new FeedAction(this, otherActor));
        }
        return actions;
    }
}
