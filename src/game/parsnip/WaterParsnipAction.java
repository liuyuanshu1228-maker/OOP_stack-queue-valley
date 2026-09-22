package game.parsnip;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.WaterCropAction;

import java.util.Random;

/**
 * The action of watering a {@link Parsnip}.
 * <p>
 * Watering a parsnip always heals the farmer, but each watering also risks
 * ruining the plant: the more the parsnip has already been watered, the more
 * likely it is to be destroyed.
 */
public class WaterParsnipAction extends WaterCropAction {

    private static final int STAMINA_COST = 1;
    private static final int HEAL_AMOUNT = 20;
    private static final int BASE_DAMAGE_CHANCE = 10;

    private final Random random = new Random();

    /**
     * Constructor.
     *
     * @param crop the parsnip to be watered
     * @param cropLocation the location of the parsnip to be watered
     */
    public WaterParsnipAction(Parsnip crop, Location cropLocation) {
        super(crop, cropLocation);
    }

    /**
     * @return the stamina cost of watering a parsnip
     */
    @Override
    protected int getStaminaCost() {
        return STAMINA_COST;
    }

    /**
     * Heals the actor, then rolls the chance for the parsnip to be damaged
     * and removed from the ground. The chance grows by one percentage point
     * for every time the parsnip has been watered so far, including this
     * watering.
     *
     * @param actor the actor watering the parsnip
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    protected String applyWateringEffect(Actor actor, GameMap map) {
        actor.heal(HEAL_AMOUNT);
        String message = actor + " waters the " + crop + " and is healed for " + HEAL_AMOUNT + " points.";

        int damageChancePercent = BASE_DAMAGE_CHANCE + crop.getWateringCount();
        if (random.nextInt(100) < damageChancePercent) {
            destroyCrop();
            message += " The " + crop + " couldn't take it and was destroyed.";
        }

        return message;
    }
}
