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
     * How many turns of age between attempts.
     */
    private final int intervalTurns;

    /**
     * The chance, as a percentage, that the ability happens on its turn.
     */
    private final int chancePercent;

    /**
     * Creates an ability that happens every given number of turns, with the
     * given chance.
     *
     * @param intervalTurns how many turns of age between attempts
     * @param chancePercent the chance, as a percentage, that it happens on its turn
     */
    protected PeriodicAbility(int intervalTurns, int chancePercent) {
        this.intervalTurns = intervalTurns;
        this.chancePercent = chancePercent;
    }

    /**
     * Creates an ability that always happens every given number of turns.
     *
     * @param intervalTurns how many turns of age between attempts
     */
    protected PeriodicAbility(int intervalTurns) {
        this(intervalTurns, ALWAYS_PERCENT);
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
        if (!RandomUtils.rollPercent(chancePercent)) {
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
}
