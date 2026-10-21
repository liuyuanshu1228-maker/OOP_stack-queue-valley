package game.status;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * The daze inflicted by a Quake Rune: for a fixed number of turns, the Actor
 * carrying it cannot act at all. That part is enforced by ConsciousActor
 * reading {@code hasStatus(IncapacitatingStatus.class)} -- Stunned itself
 * applies no separate per-turn effect, so {@link #applyEffect()} is empty.
 */
public class StunnedStatus extends IncapacitatingStatus {

    private StunnedStatus(int durationTurns) {
        super(durationTurns);
    }

    /**
     * Stuns the given Actor for the given number of turns, replacing any
     * Stun they already have rather than stacking a second one.
     *
     * @param actor the actor to stun
     * @param durationTurns how many turns the stun lasts
     */
    public static void applyTo(Actor actor, int durationTurns) {
        for (StunnedStatus existing : actor.statusesOf(StunnedStatus.class)) {
            actor.removeStatus(existing);
        }
        actor.addStatus(new StunnedStatus(durationTurns));
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
