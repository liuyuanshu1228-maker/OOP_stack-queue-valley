package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.tools.WateringTool;
import game.positions.Waterable;

/**
 * An Action that lets an Actor water the Ground at a Location with a watering tool. The
 * Ground can be Dirt or a crop: both implement {@link Waterable}. The shared checks live in
 * {@link CapabilityToolAction}; this class only chooses the stamina cost and applies the effect.
 */
public class WaterAction extends CapabilityToolAction<Waterable, WateringTool> {

    /** The Location of the Ground being watered, kept for the menu text. */
    private final Location target;

    /**
     * Creates a new WaterAction.
     *
     * @param location the Location of the Ground to water
     */
    public WaterAction(Location location) {
        super(location, Waterable.class, WateringTool.class,
                "has no watering tool to water the " + location.getGround() + ".",
                "cannot water the " + location.getGround() + ".");
        this.target = location;
    }

    /**
     * Asks the Ground for its own stamina cost, which defaults to the tool's cost.
     *
     * @param tool the watering tool the Actor carries
     * @param waterable the Ground being watered
     * @return the stamina cost of this watering
     */
    @Override
    protected int staminaCost(WateringTool tool, Waterable waterable) {
        return waterable.getWateringStaminaCost(tool.getStaminaCost());
    }

    /**
     * Lets the Ground apply the effect of being watered.
     *
     * @param actor the Actor watering the Ground
     * @param location the Location of the Ground
     * @param waterable the Ground being watered
     * @return a description of the outcome
     */
    @Override
    protected String applyEffect(Actor actor, Location location, Waterable waterable) {
        return waterable.water(actor, location);
    }

    /**
     * Tells whether the given watering tool can water the given ground.
     *
     * @param tool the watering tool being considered
     * @param waterable the Waterable capability of the Ground
     * @return true if the tool can water it
     */
    @Override
    protected boolean canUseOn(WateringTool tool, Waterable waterable) {
        return tool.canWater(waterable);
    }

    /**
     * Describes this action in the menu.
     *
     * @param actor the Actor performing the action
     * @return the menu text
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " waters the " + target.getGround() + ".";
    }
}
