package game.creaturestate;

/**
 * Capability interface for an Actor that tracks how long it has gone
 * without feeling threatened, so a state can decide when it has calmed down.
 * <p>
 * State objects are shared between creatures, so this per-creature memory
 * lives on the Actor itself rather than on the state.
 */
public interface Composure {

    /**
     * Returns how many consecutive turns this Actor has felt safe.
     *
     * @return the number of consecutive safe turns
     */
    int getSafeTurns();

    /**
     * Records one more consecutive turn in which this Actor felt safe.
     */
    void recordSafeTurn();

    /**
     * Resets the count of consecutive safe turns back to zero.
     */
    void resetComposure();
}