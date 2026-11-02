package game.plants;

/**
 * Capability interface for a plant that spreads onto nearby tiles. Only the
 * plants that can spread implement it, and a {@link SelfReplicate} ability
 * finds it through {@code location.getGroundAs(Spreading.class)}, so no plant
 * has to carry a spreading method it cannot honour.
 */
public interface Spreading {

    /**
     * Creates the plant to be planted on an adjacent tile.
     *
     * @return a new instance of the plant to plant nearby
     */
    public Plant offspring();
}
