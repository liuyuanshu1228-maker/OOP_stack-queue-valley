package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import game.status.TimedStatus;

/**
 * The burn inflicted on an Actor by standing in {@link Fire}: a fixed amount
 * of damage every turn, for a fixed number of turns.
 * <p>
 * An Actor only ever carries one burn at a time. Applying a new burn (see
 * {@link #applyTo(Actor, int, int)}) replaces the old one, which is what makes
 * a fire "reapply" its burn every turn an Actor keeps standing in it.
 */
public class BurningStatus extends TimedStatus {

    /** The actor currently affected by this burn. */
    private final Actor burningActor;

    /** HP deducted from the burning actor each turn. */
    private final int damagePerTurn;

    /**
     * Creates a new BurningStatus. Use {@link #applyTo(Actor, int, int)} to set an Actor alight.
     *
     * @param burningActor the actor being burned
     * @param damagePerTurn the damage dealt to the actor each turn
     * @param durationTurns how many turns the burn lasts
     */
    private BurningStatus(Actor burningActor, int damagePerTurn, int durationTurns) {
        super(durationTurns);
        this.burningActor = burningActor;
        this.damagePerTurn = damagePerTurn;
    }

    /**
     * Sets the given Actor burning, replacing any burn they already have.
     *
     * @param actor the actor to burn
     * @param damagePerTurn the damage dealt to the actor each turn
     * @param durationTurns how many turns the burn lasts
     */
    public static void applyTo(Actor actor, int damagePerTurn, int durationTurns) {
        for (BurningStatus existingBurn : actor.statusesOf(BurningStatus.class)) {
            actor.removeStatus(existingBurn);
        }
        actor.addStatus(new BurningStatus(actor, damagePerTurn, durationTurns));
    }

    /**
     * Deals one turn's worth of burn damage.
     */
    @Override
    protected void applyEffect() {
        burningActor.hurt(damagePerTurn);
    }

    /**
     * @return the name of this status, as shown next to the actor (Burning)
     */
    @Override
    public String toString() {
        return "Burning";
    }
}
