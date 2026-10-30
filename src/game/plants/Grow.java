package game.plants;

import edu.monash.fit2099.engine.positions.Location;
import game.random.RandomUtils;

/**
 * A plant ability that replaces the plant with its next growth stage.
 * <p>
 * The next stage is created by the plant itself, via {@link Plant#nextStage()},
 * so a new instance is created every time and each plant decides what its own
 * next stage is, e.g. a {@code StrawberrySprout}'s {@code nextStage()}
 * returns a new {@code Strawberry}.
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
     * Replaces the plant with a new instance of its next stage.
     *
     * @param plant the plant that grows
     * @param location the location of the plant
     * @return a description of the growth, to be displayed to the player
     */
    @Override
    protected String perform(Plant plant, Location location) {
        Plant next = plant.nextStage();
        location.setGround(next);
        return plant + " at " + location + " grows into a " + next + ".";
    }
}
