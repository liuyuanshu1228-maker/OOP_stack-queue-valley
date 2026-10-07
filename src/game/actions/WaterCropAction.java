package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.CropWaterable;
import game.items.tools.WateringTool;

/**
 * An Action that allows an Actor to water a {@link game.crops.Crop}, provided
 * they are carrying a watering tool and have enough stamina for that crop's
 * cost.
 * <p>
 * It shares the tool, capability and stamina checks of
 * {@link CapabilityToolAction}; the only differences are that the stamina
 * cost comes from the crop rather than the tool, and the effect records the
 * watering before applying the crop-specific outcome.
 * <p>
 * This is unrelated to A1's ground-level {@link WaterAction}, which turns
 * raw Dirt into a Puddle; this class nurtures an already-mature crop in
 * place.
 */
public class WaterCropAction extends CapabilityToolAction<CropWaterable, WateringTool> {

    /** The map location of the crop to be watered. */
    private final Location cropLocation;

    /**
     * Constructor.
     *
     * @param cropLocation the Location of the crop to be watered
     */
    public WaterCropAction(Location cropLocation) {
        super(cropLocation, CropWaterable.class, WateringTool.class,
                "has no watering tool to water the " + cropLocation.getGround() + ".",
                "cannot water the " + cropLocation.getGround() + ".");
        this.cropLocation = cropLocation;
    }

    /**
     * Each crop sets its own watering cost, regardless of the tool used.
     *
     * @param tool the watering tool being used
     * @param crop the crop being watered
     * @return the crop's watering stamina cost
     */
    @Override
    protected int staminaCost(WateringTool tool, CropWaterable crop) {
        return crop.getWateringStaminaCost();
    }

    /**
     * Records the watering on the crop, then applies the crop-specific effect.
     *
     * @param actor the actor watering the crop
     * @param location the Location of the crop
     * @param crop the crop being watered
     * @return a description of what happened
     */
    @Override
    protected String applyEffect(Actor actor, Location location, CropWaterable crop) {
        crop.recordWatering();
        return crop.water(actor, location);
    }

    /**
     * Describes this action in a format suitable for displaying in the menu.
     *
     * @param actor the actor performing the action
     * @return a string, e.g. "Farmer Bob waters the Parsnip"
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " waters the " + cropLocation.getGround();
    }
}
