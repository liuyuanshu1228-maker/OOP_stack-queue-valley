package game.wanderer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import game.statistics.StatisticAdjuster;
import game.status.TimedStatus;

/**
 * The weariness brought on by a drought forecast: drains an actor's stamina
 * for a fixed number of turns.
 * <p>
 * Modelled directly on {@code game.crops.StrawberryPoisonStatus}: a
 * {@link TimedStatus} that only needs to say what its own per-turn effect is.
 * It lives in this package, rather than in {@code game.status}, because it is
 * specific to this feature, exactly as the Strawberry statuses live in
 * {@code game.crops} rather than in {@code game.status}.
 */
public class DroughtWearinessStatus extends TimedStatus {

    /** Stamina drained from the actor on each turn the status lasts. */
    private static final int STAMINA_DRAINED_PER_TURN = 2;
    /** Number of turns the weariness lasts. */
    private static final int DURATION_TURNS = 3;

    /** The actor this status drains each turn. */
    private final Actor wearyActor;

    /**
     * Creates a new DroughtWearinessStatus.
     *
     * @param wearyActor the actor being drained
     */
    public DroughtWearinessStatus(Actor wearyActor) {
        super(DURATION_TURNS);
        this.wearyActor = wearyActor;
    }

    /**
     * Drains one turn's worth of stamina. Does nothing once the actor is
     * already out of stamina, exactly as {@code StatisticAdjuster.trySpend}
     * leaves an actor unchanged when it has too little to spend.
     */
    @Override
    protected void applyEffect() {
        new StatisticAdjuster(wearyActor).trySpend(ActorStatistics.STAMINA, STAMINA_DRAINED_PER_TURN);
    }

    /**
     * @return the name of this status, as shown next to the actor (Weary)
     */
    @Override
    public String toString() {
        return "Weary";
    }
}
