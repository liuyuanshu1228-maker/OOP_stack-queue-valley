package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Abstract base class for actions that water a {@link Crop}.
 * <p>
 * Every crop requires a watering tool and a crop-specific amount of stamina
 * to be watered. This class takes care of that common bookkeeping (checking
 * for a watering tool, checking/deducting stamina, and recording the
 * watering) and delegates the crop-specific effect of watering to
 * subclasses.
 */
public abstract class WaterCropAction extends CropAction {

    /**
     * Constructor.
     *
     * @param crop the crop to be watered
     * @param cropLocation the location of the crop to be watered
     */
    public WaterCropAction(Crop crop, Location cropLocation) {
        super(crop, cropLocation);
    }

    /**
     * Applies the crop-specific effect of being watered. Called only after
     * the watering tool and stamina checks have passed, and after the
     * stamina has been deducted and the watering has been recorded.
     *
     * @param actor the actor watering the crop
     * @param map the map the actor is on
     * @return a description of what happened
     */
    protected abstract String applyWateringEffect(Actor actor, GameMap map);

    /**
     * Waters the crop: requires the actor to carry a {@link WateringTool} and
     * to have enough stamina for the crop-specific cost. On success, the
     * stamina is deducted, the watering is recorded on the crop, and the
     * crop-specific effect is applied.
     *
     * @see edu.monash.fit2099.engine.actions.Action#execute(Actor, GameMap)
     * @param actor the actor watering the crop
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (actor.getInventory().getItemsAs(WateringTool.class).isEmpty()) {
            return actor + " has no watering tool to water the " + crop + ".";
        }

        if (!hasEnoughStamina(actor)) {
            return actor + " is too tired to water the " + crop + ".";
        }

        deductStamina(actor);
        crop.recordWatering();

        return applyWateringEffect(actor, map);
    }

    /**
     * Describes this action in a format suitable for displaying in the menu.
     *
     * @see edu.monash.fit2099.engine.actions.Action#menuDescription(Actor)
     * @param actor the actor performing the action
     * @return a string, e.g. "Farmer Bob waters the Parsnip"
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " waters the " + crop;
    }
}
