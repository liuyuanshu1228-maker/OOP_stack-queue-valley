package game.positions.farm;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.plants.Plant;
import game.positions.Plantable;

/**
 * A class representing soil on the ground, produced by tilling Dirt.
 * <p>
 * Soil is {@link Plantable}: plants spreading on their own and crops planted
 * by the Merchant can only take root on Soil.
 */
public class Soil extends Ground implements Plantable {

    /**
     * Creates a new Soil.
     */
    public Soil() {
        super(',', "Soil");
    }

    /**
     * Replaces this Soil with the given plant.
     *
     * @param plant the plant to plant
     * @param location the Location this Soil currently occupies
     */
    @Override
    public void plant(Plant plant, Location location) {
        location.setGround(plant);
    }
}
