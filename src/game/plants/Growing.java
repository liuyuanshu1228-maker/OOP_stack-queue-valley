package game.plants;

/**
 * Capability interface for a plant that grows into a later stage. Only the
 * plants that can grow implement it, and a {@link Grow} ability finds it
 * through {@code location.getGroundAs(Growing.class)}, so no plant has to
 * carry a growth method it cannot honour.
 */
public interface Growing {

    /**
     * Creates the plant this one grows into.
     *
     * @return a new instance of the next growth stage
     */
    Plant nextStage();
}
