package game.crops;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.HarvestAction;
import game.actions.WaterCropAction;
import game.positions.FreelyWaterable;
import game.positions.farm.Dirt;

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
 * adjacent tile. Crops that are also {@link CropWaterable} get the
 * {@link WaterCropAction} menu entry automatically as well. Every crop also
 * accepts a free watering (e.g. from a crow's droppings) through
 * {@link FreelyWaterable}, independent of whether it is CropWaterable.
 */
public abstract class Crop extends Ground implements Harvestable, FreelyWaterable {

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
    public void recordWatering() {
        wateringCount++;
    }

    /**
     * Applies the effect of being watered for free (e.g. by a crow's
     * droppings): counts as one ordinary watering.
     */
    @Override
    public void receiveFreeWatering() {
        recordWatering();
    }

    /**
     * Removes this crop from the ground, turning its tile back into dirt.
     *
     * @param location the location currently occupied by this crop
     */
    public void removeCrop(Location location) {
        location.setGround(new Dirt());
    }

    /**
     * Offers a harvesting action to an actor standing on or next to this crop,
     * plus a watering action if this crop is {@link CropWaterable}.
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
        if (asCapability(CropWaterable.class).isPresent()) {
            actions.add(new WaterCropAction(location));
        }
        return actions;
    }
}