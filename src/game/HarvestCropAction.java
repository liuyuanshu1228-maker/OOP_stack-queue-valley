package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Abstract base class for actions that harvest a {@link Crop}.
 * <p>
 * Every harvest costs a crop-specific amount of stamina. This class takes
 * care of that common bookkeeping (checking/deducting stamina) and delegates
 * the crop-specific effect of harvesting to subclasses.
 */
public abstract class HarvestCropAction extends CropAction {

    /**
     * Constructor.
     *
     * @param crop the crop to be harvested
     * @param cropLocation the location of the crop to be harvested
     */
    public HarvestCropAction(Crop crop, Location cropLocation) {
        super(crop, cropLocation);
    }

    /**
     * Applies the crop-specific effect of being harvested. Called only after
     * the stamina check has passed and the stamina has been deducted.
     *
     * @param actor the actor harvesting the crop
     * @param map the map the actor is on
     * @return a description of what happened
     */
    protected abstract String applyHarvestEffect(Actor actor, GameMap map);

    /**
     * Harvests the crop: requires the actor to have enough stamina for the
     * crop-specific cost. On success, the stamina is deducted and the
     * crop-specific effect is applied.
     *
     * @see edu.monash.fit2099.engine.actions.Action#execute(Actor, GameMap)
     * @param actor the actor harvesting the crop
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!hasEnoughStamina(actor)) {
            return actor + " is too tired to harvest the " + crop + ".";
        }

        deductStamina(actor);

        return applyHarvestEffect(actor, map);
    }

    /**
     * Describes this action in a format suitable for displaying in the menu.
     *
     * @see edu.monash.fit2099.engine.actions.Action#menuDescription(Actor)
     * @param actor the actor performing the action
     * @return a string, e.g. "Farmer Bob harvests the Strawberry"
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " harvests the " + crop;
    }
}
