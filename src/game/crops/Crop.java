package game.crops;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.HarvestAction;
import game.actions.Harvestable;
import game.actions.WaterAction;
import game.plants.Plant;
import game.plants.PlantAbility;
import game.positions.FreelyWaterable;
import game.positions.Waterable;
import game.positions.Plantable;
import game.positions.area.AreaFactory;
import game.positions.area.PlantableTile;
import game.positions.farm.Dirt;

import java.util.Optional;

/**
 * Abstract base class for all crops.
 * <p>
 * A crop is a mature {@link Plant} that keeps track of how many times it
 * has been watered, since several crop-specific rules (chance of damage,
 * amount of produce, etc.) depend on that count. When a crop is removed by
 * {@link #removeCrop}, e.g. after a harvest, its tile reverts to {@link Dirt};
 * being regressed is handled by {@link Plant} instead.
 * <p>
 * Every crop is {@link Harvestable}; the {@link HarvestAction} menu entry is
 * offered automatically, whether the Actor is standing on the crop or on an
 * adjacent tile. Crops that are also {@link Waterable} get the
 * {@link WaterAction} menu entry automatically as well. Every crop also
 * accepts a free watering (e.g. from a crow's droppings) through
 * {@link FreelyWaterable}, independent of whether it is Waterable.
 */
public abstract class Crop extends Plant implements Harvestable, FreelyWaterable {

    /**
     * The number of times this crop has been watered.
     */
    private int wateringCount = 0;

    /**
     * Creates a new Crop.
     *
     * @param displayChar character to display for this crop
     * @param name the name of this crop
     * @param abilities the abilities of this crop, from the highest priority to the lowest
     */
    public Crop(char displayChar, String name, PlantAbility... abilities) {
        super(displayChar, name, abilities);
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
     * Plants a new crop on a random tile next to the given one, if a tile
     * there accepts a crop. A crop that can be bought uses this to have the
     * Merchant plant the copy that was paid for.
     *
     * @param crop the new crop to plant
     * @param centre the tile to plant next to
     * @param areaFactory builds the Area of tiles next to centre
     * @return where the crop was planted, or empty if no tile next to centre accepts a crop
     */
    protected final Optional<Location> plantNextTo(Crop crop, Location centre, AreaFactory areaFactory) {
        Optional<Location> spot = areaFactory.adjacentTo(centre).filter(new PlantableTile()).randomTile();
        if (spot.isPresent()) {
            spot.get().getGroundAs(Plantable.class).plant(crop, spot.get());
        }
        return spot;
    }

    /**
     * Describes a purchase that could not go ahead because the Merchant had
     * nowhere to plant the crop. Nothing is charged in that case.
     *
     * @param buyer the actor trying to buy this crop
     * @param merchant the Merchant selling it
     * @param productName the name this crop is sold under
     * @return a description of the failed purchase
     */
    protected final String noRoomToPlant(Actor buyer, Actor merchant, String productName) {
        return buyer + " tries to buy a " + productName
                + ", but " + merchant + " has no room to plant it nearby.";
    }

    /**
     * Offers a harvesting action to an actor standing on or next to this crop,
     * plus a watering action if this crop is {@link Waterable}.
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
        if (asCapability(Waterable.class).isPresent()) {
            actions.add(new WaterAction(location));
        }
        return actions;
    }
}