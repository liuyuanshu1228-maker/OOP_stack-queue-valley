package game.plants;

import edu.monash.fit2099.engine.items.Item;

/**
 * Capability interface for a plant that drops items on nearby tiles. Only
 * the plants that produce something implement it, and a {@link Produce}
 * ability finds it through {@code location.getGroundAs(Producing.class)}, so
 * no plant has to carry a produce method it cannot honour.
 */
public interface Producing {

    /**
     * Creates the item this plant drops.
     *
     * @return a new instance of the item produced
     */
    public Item produce();
}
