package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * The healing from eating a fresh strawberry: 1 HP restored per turn for 2
 * turns.
 */
public class StrawberryHealStatus implements Status {

    public static final int HEAL_PER_TURN = 1;
    public static final int DURATION_TURNS = 2;

    private final Actor healedActor;
    private int remainingTurns = DURATION_TURNS;

    /**
     * Constructor.
     *
     * @param healedActor the actor being healed
     */
    public StrawberryHealStatus(Actor healedActor) {
        this.healedActor = healedActor;
    }

    /**
     * Restores one turn's worth of healing.
     *
     * @see Status#tickStatus(GameEntity, Location)
     * @param currentEntity the entity this status is attached to
     * @param location the location of the entity this status is attached to
     */
    @Override
    public void tickStatus(GameEntity currentEntity, Location location) {
        healedActor.heal(HEAL_PER_TURN);
        remainingTurns--;
    }

    /**
     * @return true while there are still healing turns remaining
     */
    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }

    @Override
    public String toString() {
        return "Recovering";
    }
}
