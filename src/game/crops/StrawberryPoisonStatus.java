package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import game.TimedStatus;

/**
 * The poison inflicted by eating a rotten strawberry: 1 damage point per
 * turn for 3 turns.
 */
public class StrawberryPoisonStatus extends TimedStatus {

    public static final int DAMAGE_PER_TURN = 1;
    public static final int DURATION_TURNS = 3;

    private final Actor poisonedActor;

    /**
     * Constructor.
     *
     * @param poisonedActor the actor being poisoned
     */
    public StrawberryPoisonStatus(Actor poisonedActor) {
        super(DURATION_TURNS);
        this.poisonedActor = poisonedActor;
    }

    /**
     * Deals one turn's worth of poison damage.
     */
    @Override
    protected void applyEffect() {
        poisonedActor.hurt(DAMAGE_PER_TURN);
    }

    @Override
    public String toString() {
        return "Poisoned";
    }
}
