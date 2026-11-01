package game.crops;

import game.plants.Grow;
import game.plants.Growing;
import game.plants.Plant;
import game.plants.SelfReplicate;
import game.plants.Spreading;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

/**
 * The only early growth stage of a melon plant, before the mature
 * {@link Melon} crop.
 * <p>
 * It can do two things, in priority order:
 * <ol>
 *     <li>every {@value #GROW_INTERVAL} turns, a {@value #GROW_CHANCE_PERCENT}%
 *     chance of growing into a Melon;</li>
 *     <li>every {@value #SPREAD_INTERVAL} turns, planting another
 *     MelonSprout on an adjacent tile.</li>
 * </ol>
 * <p>
 * A sprout is a {@link Plant} but not a crop: it cannot be watered or
 * harvested, and the Farmer can walk onto it. It has no earlier stage, so
 * when it is regressed it dies, leaving Dirt.
 */
public class MelonSprout extends Plant implements Growing, Spreading {

    /** How many turns between attempts to grow. */
    private static final int GROW_INTERVAL = 15;

    /** The chance, as a percentage, of growing on its turn. */
    private static final int GROW_CHANCE_PERCENT = 25;

    /** How many turns between plantings of a sprout nearby. */
    private static final int SPREAD_INTERVAL = 10;

    /** The randomness this plant hands to the Melon it grows into and its own offspring. */
    private final RandomUtils randomUtils;

    /** Builds Areas for this plant's own spreading, and for the Melon it grows into. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new Melon Sprout with an age of 0.
     *
     * @param randomUtils the randomness used to roll this plant's chances,
     *                    and handed to the Melon it grows into and its own offspring
     * @param areaFactory builds Areas for this plant's spreading, and for the
     *                    Melon it grows into
     */
    public MelonSprout(RandomUtils randomUtils, AreaFactory areaFactory) {
        super('t', "Melon Sprout",
                new Grow(GROW_INTERVAL, GROW_CHANCE_PERCENT, randomUtils),
                new SelfReplicate(SPREAD_INTERVAL, randomUtils, areaFactory));
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * A Melon Sprout grows into a new Melon.
     *
     * @return a new Melon
     */
    @Override
    public Plant nextStage() {
        return new Melon(randomUtils, areaFactory);
    }

    /**
     * A Melon Sprout spreads by planting another Melon Sprout nearby.
     *
     * @return a new MelonSprout
     */
    @Override
    public Plant offspring() {
        return new MelonSprout(randomUtils, areaFactory);
    }
}
