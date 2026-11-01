package game.crops;

import edu.monash.fit2099.engine.positions.Ground;
import game.plants.Grow;
import game.plants.Growing;
import game.plants.Plant;
import game.plants.SelfReplicate;
import game.plants.Spreading;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

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
public class StrawberrySprout extends Plant implements Growing, Spreading {

    /** How many turns between attempts to grow. */
    private static final int GROW_INTERVAL = 10;

    /** The chance, as a percentage, of growing on its turn. */
    private static final int GROW_CHANCE_PERCENT = 50;

    /** How many turns between plantings of a seedling nearby. */
    private static final int SPREAD_INTERVAL = 5;

    /** The randomness this plant hands to its neighbouring stages. */
    private final RandomUtils randomUtils;

    /** Builds Areas for this plant's own spreading, and for its neighbouring stages. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new Strawberry Sprout with an age of 0.
     *
     * @param randomUtils the randomness used to roll this plant's chances,
     *                    and handed to its neighbouring stages
     * @param areaFactory builds Areas for this plant's spreading, and for its
     *                    neighbouring stages
     */
    public StrawberrySprout(RandomUtils randomUtils, AreaFactory areaFactory) {
        super('v', "Strawberry Sprout",
                new Grow(GROW_INTERVAL, GROW_CHANCE_PERCENT, randomUtils),
                new SelfReplicate(SPREAD_INTERVAL, randomUtils, areaFactory));
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * A Strawberry Sprout grows into a new Strawberry.
     *
     * @return a new Strawberry
     */
    @Override
    public Plant nextStage() {
        return new Strawberry(randomUtils, areaFactory);
    }

    /**
     * A Strawberry Sprout spreads by planting a new Strawberry Seedling.
     *
     * @return a new StrawberrySeedling
     */
    @Override
    public Plant offspring() {
        return new StrawberrySeedling(randomUtils, areaFactory);
    }

    /**
     * A regressed Strawberry Sprout becomes a new Strawberry Seedling.
     *
     * @return a new StrawberrySeedling
     */
    @Override
    protected Ground previousStage() {
        return new StrawberrySeedling(randomUtils, areaFactory);
    }
}
