package game.crops;

import edu.monash.fit2099.engine.positions.Ground;
import game.plants.Grow;
import game.plants.Plant;
import game.plants.SelfReplicate;

/**
 * The second growth stage of a strawberry plant, between
 * {@link StrawberrySeedling} and the mature {@link Strawberry} crop.
 * <p>
 * It can do two things, in priority order:
 * <ol>
 *     <li>every {@value #GROW_INTERVAL} turns, a {@value #GROW_CHANCE_PERCENT}%
 *     chance of growing into a Strawberry;</li>
 *     <li>every {@value #SPREAD_INTERVAL} turns, planting a
 *     StrawberrySeedling on an adjacent tile.</li>
 * </ol>
 * <p>
 * A sprout is a {@link Plant} but not a crop: it cannot be watered or
 * harvested, and the Farmer can walk onto it. When it is regressed it goes
 * back to being a {@link StrawberrySeedling}.
 */
public class StrawberrySprout extends Plant {

    /** How many turns between attempts to grow. */
    private static final int GROW_INTERVAL = 10;

    /** The chance, as a percentage, of growing on its turn. */
    private static final int GROW_CHANCE_PERCENT = 50;

    /** How many turns between plantings of a seedling nearby. */
    private static final int SPREAD_INTERVAL = 5;

    /**
     * Creates a new Strawberry Sprout with an age of 0.
     */
    public StrawberrySprout() {
        super('v', "Strawberry Sprout",
                new Grow(GROW_INTERVAL, GROW_CHANCE_PERCENT),
                new SelfReplicate(SPREAD_INTERVAL));
    }

    /**
     * A Strawberry Sprout grows into a new Strawberry.
     *
     * @return a new Strawberry
     */
    @Override
    protected Plant nextStage() {
        return new Strawberry();
    }

    /**
     * A Strawberry Sprout spreads by planting a new Strawberry Seedling.
     *
     * @return a new StrawberrySeedling
     */
    @Override
    protected Plant offspring() {
        return new StrawberrySeedling();
    }

    /**
     * A regressed Strawberry Sprout becomes a new Strawberry Seedling.
     *
     * @return a new StrawberrySeedling
     */
    @Override
    protected Ground previousStage() {
        return new StrawberrySeedling();
    }
}
