package game.crops;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * The poison inflicted by eating a rotten strawberry: 1 damage point per
 * turn for 3 turns.
 */
public class StrawberryPoisonStatus implements Status {

    public static final int DAMAGE_PER_TURN = 1;
    public static final int DURATION_TURNS = 3;

    private final Actor poisonedActor;
    private int remainingTurns = DURATION_TURNS;

    /**
     * Constructor.
     *
     * @param poisonedActor the actor being poisoned
     */
    public StrawberryPoisonStatus(Actor poisonedActor) {
        this.poisonedActor = poisonedActor;
    }

    /**
     * Deals one turn's worth of poison damage.
     *
     * @see Status#tickStatus(GameEntity, Location)
     * @param currentEntity the entity this status is attached to
     * @param location the location of the entity this status is attached to
     */
    @Override
    public void tickStatus(GameEntity currentEntity, Location location) {
        poisonedActor.hurt(DAMAGE_PER_TURN);
        remainingTurns--;
    }

    /**
     * @return true while there are still poison turns remaining
     */
    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }

    @Override
    public String toString() {
        return "Poisoned";
    }
}