package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A {@link Status} whose effect is applied once per turn for a fixed number
 * of turns, then expires on its own.
 * <p>
 * This is the shared shape behind every timed, self-expiring status in the
 * game (healing, poison, burning, and similar): count down a fixed number of
 * turns, applying one subclass-specific effect each turn, and report the
 * status as inactive once the count reaches zero. Subclasses only need to
 * say what the per-turn effect is and how many turns it lasts; the counting
 * itself is implemented here exactly once.
 */
public abstract class TimedStatus implements Status {

    /** Number of turns remaining before this status expires. */
    private int remainingTurns;

    /**
     * Creates a new TimedStatus.
     *
     * @param durationTurns how many turns this status lasts
     */
    protected TimedStatus(int durationTurns) {
        this.remainingTurns = durationTurns;
    }

    /**
     * Applies one turn's worth of this status's effect, then decrements the
     * remaining duration. The counting is identical for every timed status,
     * so it is implemented here once and sealed with {@code final};
     * subclasses only supply {@link #applyEffect()}.
     *
     * @see Status#tickStatus(GameEntity, Location)
     * @param currentEntity the entity this status is attached to
     * @param location the location of the entity this status is attached to
     */
    @Override
    public final void tickStatus(GameEntity currentEntity, Location location) {
        applyEffect();
        remainingTurns--;
    }

    /**
     * @return true while there are still turns remaining
     */
    @Override
    public final boolean isStatusActive() {
        return remainingTurns > 0;
    }

    /**
     * The effect this status applies on a single turn (healing, damage,
     * etc.). Called once per turn by {@link #tickStatus(GameEntity, Location)}.
     */
    protected abstract void applyEffect();

    /**
     * Attaches this status to the given Actor, first removing any status of
     * the same concrete type the Actor already carries.
     * <p>
     * This is for statuses that should not stack (e.g. a fresh Burn resets
     * the clock on an old one rather than adding a second, independent
     * Burn). A status that is meant to stack instead (healing, poison) is
     * simply added directly with {@code actor.addStatus(...)} and does not
     * call this method.
     *
     * @param actor the Actor to attach this status to
     */
    public final void replaceOn(Actor actor) {
        for (TimedStatus existing : actor.statusesOf(getClass())) {
            actor.removeStatus(existing);
        }
        actor.addStatus(this);
    }
}
