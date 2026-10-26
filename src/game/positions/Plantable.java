package game.positions;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Capability interface for Ground that a plant can be planted on, e.g. by a
 * plant self-replicating or by the Merchant planting a bought crop.
 * Callers find it through {@code location.getGroundAs(Plantable.class)}, so
 * new kinds of plantable ground can be added without changing them.
 * Implementing classes define what planting on them does.
 * <p>
 * The plant is taken as a {@link Ground} rather than a plant type, so that
 * this package does not depend on the plant packages.
 */
public interface Plantable {

    /**
     * Plants the given plant on this ground at the given Location.
     *
     * @param plant the plant to plant
     * @param location the Location this ground currently occupies
     */
    void plant(Ground plant, Location location);
}
