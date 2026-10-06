package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.tools.WateringTool;
import game.positions.Waterable;

/**
 * An Action that allows an Actor to water the Ground at their current Location,
 * provided they are carrying a Watering Can and have enough stamina.
 * The Ground must implement Waterable to define what it becomes once watered.
 * <p>
 * This is unrelated to watering a mature {@link game.crops.Crop}: this action turns
 * one type of Ground into another (e.g. Dirt into a Puddle), whereas watering a
 * crop nurtures it in place and is handled by {@link game.actions.WaterCropAction}.
 */
public class WaterAction extends CapabilityToolAction<Waterable, WateringTool> {

    /**
     * Constructor.
     *
     * @param location the Location whose Ground is to be watered
     */
    public WaterAction(Location location) {
        super(location, Waterable.class, WateringTool.class,
                "has no watering can to water the ground.", "cannot water this ground.");
    }

    /**
     * Waters the Ground, replacing it with the Waterable's water result.
     *
     * @param actor the actor performing the action
     * @param location the Location whose Ground is to be watered
     * @param waterable the Waterable capability of the Ground
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    protected String applyEffect(Actor actor, Location location, Waterable waterable) {
        location.setGround(waterable.getWaterResult());
        return actor + " waters the ground.";
    }

    /**
     * Describes what this Action will do if chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " waters the ground here.";
    }
}