package game;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.HarvestAction;
import game.ground.Harvestable;

/**
 * Abstract base class for all crops.
 * <p>
 * A crop is a type of {@link Ground} that keeps track of how many times it
 * has been watered, since several crop-specific rules (chance of damage,
 * amount of produce, etc.) depend on that count. When a crop is removed from
 * the game, for any reason, its tile reverts to {@link Dirt}.
 * <p>
 * Every crop is {@link Harvestable}; the {@link HarvestAction} menu entry is
 * offered automatically, whether the Actor is standing on the crop or on an
 * adjacent tile. Subclasses that can also be watered add their own watering
 * action on top of this.
 */
public abstract class Crop extends Ground implements Harvestable {

    /**
     * The number of times this crop has been watered.
     */
    private int wateringCount = 0;

    /**
     * Constructor.
     *
     * @param displayChar character to display for this crop
     * @param name the name of this crop
     */
    public Crop(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * How many times this crop has been watered so far.
     *
     * @return the number of times this crop has been watered
     */
    public int getWateringCount() {
        return wateringCount;
    }

    /**
     * Records that this crop has been watered once.
     */
    protected void recordWatering() {
        wateringCount++;
    }

    /**
     * Removes this crop from the ground, turning its tile back into dirt.
     *
     * @param location the location currently occupied by this crop
     */
    protected void removeCrop(Location location) {
        location.setGround(new Dirt());
    }

    /**
     * Offers a harvesting action to an actor standing on or next to this crop.
     * Subclasses that can also be watered should override this, call
     * {@code super.allowableActions(...)}, and add their own watering action.
     *
     * @param actor the actor acting
     * @param location the current location of this crop
     * @param direction the direction of this crop from the actor
     * @return the allowable actions for this crop
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        actions.add(new HarvestAction(location));
        return actions;
    }
}
