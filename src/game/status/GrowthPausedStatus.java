package game.status;

/**
 * A status that stops a plant from doing anything for a fixed number of
 * turns, e.g. because of a cold spell.
 * <p>
 * The status has no effect of its own: the plant checks for it on its turn
 * and skips its abilities while it is active, since deciding whether to grow
 * is the plant's own responsibility. The plant still ages while paused.
 * {@link TimedStatus} counts down the turns and removes it.
 */
public class GrowthPausedStatus extends TimedStatus {

    /**
     * Creates a new GrowthPausedStatus.
     *
     * @param durationTurns how many turns the plant's growth is paused for
     */
    public GrowthPausedStatus(int durationTurns) {
        super(durationTurns);
    }

    /**
     * Does nothing, as the plant itself checks for this status.
     */
    @Override
    protected void applyEffect() {
    }
}
