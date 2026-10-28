package game.positions;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A ground that covers another ground for a limited number of turns, then
 * puts the original ground back, such as slime, salt or a trap.
 * <p>
 * The countdown and the restoring are written once here. A subclass only says
 * what it does each turn, by overriding {@link #tickEffect(Location)}, and may
 * end early with {@link #revertAt(Location)}.
 */
public abstract class TemporaryGround extends Ground {

    /** The ground that was on the tile before this one, put back at the end. */
    private final Ground originalGround;

    /** Number of turns left before the original ground is put back. */
    private int remainingTurns;

    /**
     * Creates a temporary ground.
     *
     * @param displayChar the character shown on the map
     * @param name the name of this ground
     * @param originalGround the ground this one covers, put back at the end
     * @param durationTurns how many turns this ground lasts; at least 1
     * @throws IllegalArgumentException if durationTurns is less than 1
     */
    protected TemporaryGround(char displayChar, String name, Ground originalGround,
                              int durationTurns) {
        super(displayChar, name);
        if (durationTurns < 1) {
            throw new IllegalArgumentException("durationTurns must be at least 1");
        }
        this.originalGround = originalGround;
        this.remainingTurns = durationTurns;
    }

    /**
     * Applies this ground's effect for the turn, then counts down and puts the
     * original ground back when the time is up. The order is fixed here, so a
     * subclass cannot forget the countdown.
     *
     * @param location the location of this ground
     */
    @Override
    public final void tick(Location location) {
        tickEffect(location);
        if (location.getGround() != this) {
            return;
        }
        remainingTurns--;
        if (remainingTurns <= 0) {
            revertAt(location);
        }
    }

    /**
     * What this ground does each turn. Does nothing by default.
     *
     * @param location the location of this ground
     */
    protected void tickEffect(Location location) {
    }

    /**
     * Puts the original ground back now, before the time is up.
     *
     * @param location the location of this ground
     */
    protected final void revertAt(Location location) {
        location.setGround(originalGround);
    }
}
