package game.crops;

import game.plants.Grow;
import game.plants.Growing;
import game.plants.Plant;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

/**
 * The only early growth stage of a cauliflower plant, before the mature
 * {@link Cauliflower} crop.
 * <p>
 * Every {@value #GROW_INTERVAL} turns it has a {@value #GROW_CHANCE_PERCENT}%
 * chance of growing into a Cauliflower.
 * <p>
 * A sprout is a {@link Plant} but not a crop: it cannot be watered or
 * harvested, and the Farmer can walk onto it. It has no earlier stage, so
 * when it is regressed it dies, leaving Dirt.
 */
public class CauliflowerSprout extends Plant implements Growing {

    /** How many turns between attempts to grow. */
    private static final int GROW_INTERVAL = 15;

    /** The chance, as a percentage, of growing on its turn. */
    private static final int GROW_CHANCE_PERCENT = 25;

    /** The randomness this plant hands to the Cauliflower it grows into. */
    private final RandomUtils randomUtils;

    /**
     * Builds Areas for the Cauliflower this plant grows into; this sprout
     * does not use it itself (it has no spreading ability of its own), only
     * forwards it, since {@link Cauliflower} needs one for watering.
     */
    private final AreaFactory areaFactory;

    /**
     * Creates a new Cauliflower Sprout with an age of 0.
     *
     * @param randomUtils the randomness used to roll this plant's chances,
     *                    and handed to the Cauliflower it grows into
     * @param areaFactory builds Areas for the Cauliflower this plant grows into
     */
    public CauliflowerSprout(RandomUtils randomUtils, AreaFactory areaFactory) {
        super('y', "Cauliflower Sprout",
                new Grow(GROW_INTERVAL, GROW_CHANCE_PERCENT, randomUtils));
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * A Cauliflower Sprout grows into a new Cauliflower.
     *
     * @return a new Cauliflower
     */
    @Override
    public Plant nextStage() {
        return new Cauliflower(randomUtils, areaFactory);
    }
}
