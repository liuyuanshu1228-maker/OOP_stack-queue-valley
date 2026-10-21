package game.status;

/**
 * A TimedStatus that, while active, prevents the Actor carrying it from
 * acting on their turn.
 * <p>
 * This is a pure type marker: it adds no methods of its own. It exists so
 * that {@code ConsciousActor} can ask "does this actor have any status that
 * stops them acting right now" with a single
 * {@code hasStatus(IncapacitatingStatus.class)} check, without needing to
 * know about every concrete incapacitating status (Stunned, and eventually
 * Trapped) individually. Each subclass only needs to say how long it lasts
 * and, via TimedStatus's {@code applyEffect()}, what its own per-turn effect
 * (if any) is on top of the inability to act.
 */
public abstract class IncapacitatingStatus extends TimedStatus {

    /**
     * @param durationTurns how many turns this status lasts
     */
    protected IncapacitatingStatus(int durationTurns) {
        super(durationTurns);
    }
}
