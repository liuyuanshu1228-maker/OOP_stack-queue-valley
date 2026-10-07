package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import game.TimedStatus;

/**
 * The healing from eating a fresh strawberry: 1 HP restored per turn for 2
 * turns.
 */
public class StrawberryHealStatus extends TimedStatus {

    public static final int HEAL_PER_TURN = 1;
    public static final int DURATION_TURNS = 2;

    private final Actor healedActor;

    /**
     * Constructor.
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

    @Override
    public String toString() {
        return "Recovering";
    }
}
