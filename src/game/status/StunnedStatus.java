package game.status;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * The daze inflicted by a Quake Rune: for a fixed number of turns, the Actor
 * carrying it cannot act at all. That part is enforced by ConsciousActor
 * reading {@code hasStatus(IncapacitatingStatus.class)} -- Stunned itself
 * applies no separate per-turn effect, so {@link #applyEffect()} is empty.
 */
public class StunnedStatus extends IncapacitatingStatus {

    /**
     * Creates a new StunnedStatus. Use {@link TimedStatus#replaceOn(Actor)}
     * to attach it to an Actor, replacing any Stun they already have rather
     * than stacking a second one.
     *
     * @param durationTurns how many turns the stun lasts
     */
    public StunnedStatus(int durationTurns) {
        super(durationTurns);
    }

    @Override
    protected void applyEffect() {
        // Intentionally empty: being stunned has no per-turn effect of its
        // own. The actual effect -- not being able to act -- is read by
        // ConsciousActor via hasStatus(IncapacitatingStatus.class).
    }

    @Override
    public String toString() {
        return "Stunned";
    }
}
