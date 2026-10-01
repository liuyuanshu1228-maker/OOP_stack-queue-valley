package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.Cauliflower;
import game.positions.Crop;
import game.positions.Puddle;

import java.util.Random;

/**
 * The action of watering a {@link Cauliflower}.
 * <p>
 * There is a chance that every non-crop ground tile adjacent to the crop
 * turns into a {@link Puddle}. Adjacent crops are never converted.
 */
public class WaterCauliflowerAction extends WaterCropAction {

    private static final int STAMINA_COST = 2;
    private static final int PUDDLE_CHANCE_PERCENT = 50;

    private final Random random = new Random();

    /**
     * Constructor.
     *
     * @param crop the cauliflower to be watered
     * @param cropLocation the location of the cauliflower to be watered
     */
    public WaterCauliflowerAction(Cauliflower crop, Location cropLocation) {
        super(crop, cropLocation);
    }

    /**
     * @return the stamina cost of watering a cauliflower
     */
    @Override
    protected int getStaminaCost() {
        return STAMINA_COST;
    }

    /**
     * Rolls the chance to turn the adjacent, non-crop ground tiles into
     * puddles.
     *
     * @param actor the actor watering the cauliflower
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    protected String applyWateringEffect(Actor actor, GameMap map) {
        String message = actor + " waters the " + crop + ".";

        if (random.nextInt(100) < PUDDLE_CHANCE_PERCENT) {
            for (Location adjacentLocation : cropLocation.getNearbyLocations(1)) {
                if (adjacentLocation.getGroundAs(Crop.class) == null) {
                    adjacentLocation.setGround(new Puddle());
                }
            }
            message += " The surrounding ground turns into puddles.";
        }

        return message;
    }
}
