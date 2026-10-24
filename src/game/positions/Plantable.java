package game.positions;

import edu.monash.fit2099.engine.positions.Location;
import game.plants.Plant;

/**
 * Capability interface for Ground that a plant can be planted on, e.g. by a
 * plant self-replicating or by the Merchant planting a bought crop.
 * Callers find it through {@code location.getGroundAs(Plantable.class)}, so
 * new kinds of plantable ground can be added without changing them.
 * Implementing classes define what planting on them does.
 */
public interface Plantable {

    /**
     * Plants the given plant on this ground at the given Location.
     *
     * @param plant the plant to plant
     * @param location the Location this ground currently occupies
     */
    void plant(Plant plant, Location location);
}
