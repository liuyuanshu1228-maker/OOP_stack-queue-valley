package game.strawberry;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.HarvestCropAction;

import java.util.Random;

/**
 * The action of harvesting a {@link Strawberry}.
 * <p>
 * Harvesting always yields two strawberries, but also risks destroying the
 * plant: the more the strawberry has already been watered, the safer the
 * harvest.
 */
class HarvestStrawberryAction extends HarvestCropAction {

    private static final int STAMINA_COST = 1;
    private static final int STRAWBERRIES_PER_HARVEST = 2;
    private static final int BASE_DESTRUCTION_CHANCE_PERCENT = 50;

    private final Random random = new Random();

    /**
     * Constructor.
     *
     * @param crop the strawberry to be harvested
     * @param cropLocation the location of the strawberry to be harvested
     */
    HarvestStrawberryAction(Strawberry crop, Location cropLocation) {
        super(crop, cropLocation);
    }

    /**
     * @return the stamina cost of harvesting a strawberry
     */
    @Override
    protected int getStaminaCost() {
        return STAMINA_COST;
    }

    /**
     * Adds two strawberries to the actor's inventory, then rolls the chance
     * for the plant to be destroyed. The chance shrinks by one percentage
     * point for every time the strawberry has been watered.
     *
     * @param actor the actor harvesting the strawberry
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    protected String applyHarvestEffect(Actor actor, GameMap map) {
        for (int i = 0; i < STRAWBERRIES_PER_HARVEST; i++) {
            actor.getInventory().add(StrawberryItem.createRandom());
        }

        String message = actor + " harvests the " + crop + " and picks up 2 strawberries.";

        int destructionChancePercent = BASE_DESTRUCTION_CHANCE_PERCENT - crop.getWateringCount();
        if (random.nextInt(100) < destructionChancePercent) {
            destroyCrop();
            message += " The " + crop + " couldn't withstand the harvest and was destroyed.";
        }

        return message;
    }
}
