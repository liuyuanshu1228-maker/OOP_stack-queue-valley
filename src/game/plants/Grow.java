package game.plants;

import edu.monash.fit2099.engine.positions.Location;

import java.util.function.Supplier;

/**
 * A plant ability that replaces the plant with its next growth stage.
 * <p>
 * The next stage is a brand-new plant, so its age starts again from 0. It is
 * given as a {@link Supplier} so that a new instance is created every time,
 * e.g. {@code new Grow(5, 50, StrawberrySprout::new)}.
 */
public class Grow extends PeriodicAbility {

    /**
     * Creates the plant this one grows into.
     */
    private final Supplier<Plant> nextStage;

    /**
     * Creates a Grow ability.
     *
     * @param intervalTurns how many turns of age between attempts to grow
     * @param chancePercent the chance, as a percentage, of growing on its turn
     * @param nextStage creates the plant this one grows into
     */
    public Grow(int intervalTurns, int chancePercent, Supplier<Plant> nextStage) {
        super(intervalTurns, chancePercent);
        this.nextStage = nextStage;
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
        Plant next = nextStage.get();
        location.setGround(next);
        return plant + " at " + location + " grows into a " + next + ".";
    }
}
