package game.wanderer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import game.statistics.StatisticAdjuster;
import game.statistics.StatisticChange;
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
    /** How the actor's stamina changed on the turn just applied. */
    private StatisticChange lastDrain;

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
     * Drains one turn's worth of stamina. An actor with less than that left
     * loses what it has, so its stamina stops at zero.
     */
    @Override
    protected void applyEffect() {
        lastDrain = new StatisticAdjuster(wearyActor).decrease(ActorStatistics.STAMINA, STAMINA_DRAINED_PER_TURN);
    }

    /**
     * Reports how much stamina the drought drained this turn, and where.
     *
     * @param location the location of the weary actor
     * @return a description of this turn's drain, for example
     *         "Farmer is weary from the drought at (3, 4) on Farm: -2 stamina (10 -> 8)."
     */
    @Override
    protected String describeTurn(Location location) {
        return wearyActor + " is weary from the drought at " + location + ": "
                + lastDrain.describe("stamina") + ".";
    }

    /**
     * @return the name of this status, as shown next to the actor (Weary)
     */
    @Override
    public String toString() {
        return "Weary";
    }
}
