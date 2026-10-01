package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.StrawberryItem;
import game.positions.Strawberry;

import java.util.List;
import java.util.Random;

/**
 * The action of watering a {@link Strawberry}.
 * <p>
 * Each watering drops two strawberries onto adjacent tiles, chosen
 * independently and at random, so both may land on the same tile.
 */
public class WaterStrawberryAction extends WaterCropAction {

    private static final int STAMINA_COST = 1;
    private static final int STRAWBERRIES_PER_WATERING = 2;

    private final Random random = new Random();

    /**
     * Constructor.
     *
     * @param crop the strawberry to be watered
     * @param cropLocation the location of the strawberry to be watered
     */
    public WaterStrawberryAction(Strawberry crop, Location cropLocation) {
        super(crop, cropLocation, STAMINA_COST);
    }

    /**
     * Drops two strawberries onto random tiles adjacent to the crop.
     *
     * @param actor the actor watering the strawberry
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    protected String applyWateringEffect(Actor actor, GameMap map) {
        List<Location> adjacentLocations = cropLocation.getNearbyLocations(1);

        for (int i = 0; i < STRAWBERRIES_PER_WATERING; i++) {
            Location target = adjacentLocations.get(random.nextInt(adjacentLocations.size()));
            target.addItem(StrawberryItem.createRandom());
        }

        return actor + " waters the " + crop + ". Strawberries appear on the nearby tiles.";
    }
}
