package game.plants;

import edu.monash.fit2099.engine.positions.Location;
import game.random.RandomUtils;

/**
 * Abstract base class for plant abilities that happen every N turns of the
 * plant's age, possibly with a chance of failing.
 * <p>
 * "Every N turns" means the turns where the plant's age is a multiple of N:
 * N, 2N, 3N and so on. {@link #tryPerform} is final, so every subclass shares
 * the same timing rule and only writes its own effect in {@link #perform}.
 */
public abstract class PeriodicAbility implements PlantAbility {

    /**
     * The chance used when an ability always happens on its turn.
     */
    private static final int ALWAYS_PERCENT = 100;

    /**
     * The chance used when an ability never happens, the smallest chance allowed.
     */
    private static final int NEVER_PERCENT = 0;

    /**
     * How many turns of age between attempts.
     */
    private final int intervalTurns;

    /**
     * The chance, as a percentage, that the ability happens on its turn.
     */
    private final int chancePercent;

    /**
     * The randomness used to roll the chance.
     */
    private final RandomUtils randomUtils;

    /**
     * Creates an ability that happens every given number of turns, with the
     * given chance.
     *
     * @param intervalTurns how many turns of age between attempts; must be positive
     * @param chancePercent the chance, as a percentage, that it happens on its turn;
     *                      must be from 0 to 100
     * @param randomUtils the randomness used to roll the chance
     * @throws IllegalArgumentException if the interval is not positive or the
     *                                  chance is outside 0 to 100
     */
    protected PeriodicAbility(int intervalTurns, int chancePercent, RandomUtils randomUtils) {
        if (intervalTurns <= 0) {
            throw new IllegalArgumentException("intervalTurns must be positive, but was " + intervalTurns);
        }
        if (chancePercent < NEVER_PERCENT || chancePercent > ALWAYS_PERCENT) {
            throw new IllegalArgumentException("chancePercent must be from " + NEVER_PERCENT
                    + " to " + ALWAYS_PERCENT + ", but was " + chancePercent);
        }
        this.intervalTurns = intervalTurns;
        this.chancePercent = chancePercent;
        this.randomUtils = randomUtils;
    }

    /**
     * Creates an ability that always happens every given number of turns.
     *
     * @param intervalTurns how many turns of age between attempts; must be positive
     * @param randomUtils the randomness used to roll the chance
     * @throws IllegalArgumentException if the interval is not positive
     */
    protected PeriodicAbility(int intervalTurns, RandomUtils randomUtils) {
        this(intervalTurns, ALWAYS_PERCENT, randomUtils);
    }

    /**
     * Performs the effect if the plant's age is a multiple of the interval
     * and the chance roll succeeds. The turn is checked before rolling, so no
     * random number is used on the other turns.
     *
     * @param plant the plant using this ability
     * @param location the location of the plant
     * @return a description of what happened, or an empty string if nothing happened
     */
    @Override
    public final String tryPerform(Plant plant, Location location) {
        if (plant.getAge() % intervalTurns != 0) {
            return "";
        }
        if (!randomUtils.rollPercent(chancePercent)) {
            return "";
        }
        return perform(plant, location);
    }

    /**
     * The effect of this ability, once its turn has come and the chance
     * roll has succeeded.
     *
     * @param plant the plant using this ability
     * @param location the location of the plant
     * @return a description of what happened, or an empty string if the
     *         effect could not happen, e.g. because there is no room
     */
    protected abstract String perform(Plant plant, Location location);

    /**
     * Performs this ability's effect immediately, skipping the interval and
     * chance checks {@link #tryPerform} normally applies. Final for the same
     * reason {@link #tryPerform} is: every subclass must skip those checks
     * the same way, and {@link #perform} remains the only extension point.
     *
     * @param plant the plant using this ability
     * @param location the location of the plant
     * @return a description of what happened, or an empty string if the
     *         effect could not happen, e.g. because there is no room
     */
    @Override
    public final String forcePerform(Plant plant, Location location) {
        return perform(plant, location);
    }
}
