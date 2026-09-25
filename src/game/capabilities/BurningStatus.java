package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Fire;

/**
 * The burn inflicted on an Actor by standing in {@link Fire}: a fixed amount
 * of damage every turn, for a fixed number of turns.
 * <p>
 * An Actor only ever carries one burn at a time. Applying a new burn (see
 * {@link #applyTo(Actor, int, int)}) replaces the old one, which is what makes
 * a fire "reapply" its burn every turn an Actor keeps standing in it.
 */
public class BurningStatus implements Status {

    private final Actor burningActor;
    private final int damagePerTurn;
    private int remainingTurns;

    /**
     * Constructor. Use {@link #applyTo(Actor, int, int)} to set an Actor alight.
     *
     * @param burningActor the actor being burned
     * @param damagePerTurn the damage dealt to the actor each turn
     * @param durationTurns how many turns the burn lasts
     */
    private BurningStatus(Actor burningActor, int damagePerTurn, int durationTurns) {
        this.burningActor = burningActor;
        this.damagePerTurn = damagePerTurn;
        this.remainingTurns = durationTurns;
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
     *
     * @see Status#tickStatus(GameEntity, Location)
     * @param currentEntity the entity this status is attached to
     * @param location the location of the entity this status is attached to
     */
    @Override
    public void tickStatus(GameEntity currentEntity, Location location) {
        burningActor.hurt(damagePerTurn);
        remainingTurns--;
    }

    /**
     * @return true while there are still burning turns remaining
     */
    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }

    @Override
    public String toString() {
        return "Burning";
    }
}