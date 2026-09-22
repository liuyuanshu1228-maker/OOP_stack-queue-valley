package game.melon;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.Chance;
import game.Stats;
import game.WaterCropAction;

/**
 * The action of watering a {@link Melon}.
 * <p>
 * Each watering has a chance of permanently raising the farmer's maximum
 * stamina, without topping up their current stamina to match.
 */
class WaterMelonAction extends WaterCropAction {

    private static final int STAMINA_COST = 1;
    private static final int MAX_STAMINA_INCREASE_CHANCE_PERCENT = 50;
    private static final int MAX_STAMINA_INCREASE_AMOUNT = 1;

    /**
     * Constructor.
     *
     * @param crop the melon to be watered
     * @param cropLocation the location of the melon to be watered
     */
    WaterMelonAction(Melon crop, Location cropLocation) {
        super(crop, cropLocation);
    }

    /**
     * @return the stamina cost of watering a melon
     */
    @Override
    protected int getStaminaCost() {
        return STAMINA_COST;
    }

    /**
     * Rolls the chance to raise the actor's maximum stamina.
     *
     * @param actor the actor watering the melon
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    protected String applyWateringEffect(Actor actor, GameMap map) {
        String message = actor + " waters the " + crop + ".";

        if (Chance.rollPercent(MAX_STAMINA_INCREASE_CHANCE_PERCENT)) {
            Stats.increaseMaximumWithoutRestoring(actor, ActorStatistics.STAMINA, MAX_STAMINA_INCREASE_AMOUNT);
            message += " Its maximum stamina increases by " + MAX_STAMINA_INCREASE_AMOUNT + ".";
        }

        return message;
    }
}
