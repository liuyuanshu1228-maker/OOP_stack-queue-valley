package game.plants;

import edu.monash.fit2099.engine.positions.Location;
import game.random.RandomUtils;

/**
 * A plant ability that replaces the plant with its next growth stage.
 * <p>
 * The next stage is created by the plant itself, via {@link Growing#nextStage()},
 * so a new instance is created every time and each plant decides what its own
 * next stage is, e.g. a {@code StrawberrySprout}'s {@code nextStage()}
 * returns a new {@code Strawberry}. A plant that is not {@link Growing} is
 * left as it is.
 */
public class Grow extends PeriodicAbility {

    /**
     * Creates a Grow ability.
     *
     * @param intervalTurns how many turns of age between attempts to grow
     * @param chancePercent the chance, as a percentage, of growing on its turn
     * @param randomUtils the randomness used to roll the chance
     */
    public Grow(int intervalTurns, int chancePercent, RandomUtils randomUtils) {
        super(intervalTurns, chancePercent, randomUtils);
    }

    /**
     * Replaces the plant with a new instance of its next stage, if it is
     * {@link Growing}.
     *
     * @param plant the plant that grows
     * @param location the location of the plant
     * @return a description of the growth, or an empty string if the plant
     * cannot grow
     */
    @Override
    protected String perform(Plant plant, Location location) {
        Growing growing = location.getGroundAs(Growing.class);
        if (growing == null) {
            return "";
        }
        Plant next = growing.nextStage();
        location.setGround(next);
        return plant + " at " + location + " grows into a " + next + ".";
    }
}
