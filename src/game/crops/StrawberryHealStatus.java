package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import game.status.TimedStatus;

/**
 * The healing from eating a fresh strawberry: 1 HP restored per turn for 2
 * turns.
 */
public class StrawberryHealStatus extends TimedStatus {

    /** HP restored to the actor on each turn the status lasts. */
    private static final int HEAL_PER_TURN = 1;
    /** Number of turns the healing lasts. */
    private static final int DURATION_TURNS = 2;

    private final Actor healedActor;

    /**
     * Creates a new StrawberryHealStatus.
     *
     * @param healedActor the actor being healed
     */
    public StrawberryHealStatus(Actor healedActor) {
        super(DURATION_TURNS);
        this.healedActor = healedActor;
    }

    /**
     * Restores one turn's worth of healing.
     */
    @Override
    protected void applyEffect() {
        healedActor.heal(HEAL_PER_TURN);
    }

    /**
     * Describes this status's own effect, so callers do not need to know its
     * internal heal amount or duration.
     *
     * @return a description of the healing, e.g. "healed for 1 HP per turn over 2 turns"
     */
    public String describeEffect() {
        return "healed for " + HEAL_PER_TURN + " HP per turn over " + DURATION_TURNS + " turns";
    }

    /**
     * @return the name of this status, as shown next to the actor (Recovering)
     */
    @Override
    public String toString() {
        return "Recovering";
    }
}
