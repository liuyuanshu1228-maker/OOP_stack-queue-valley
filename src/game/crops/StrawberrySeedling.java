package game.crops;

import game.plants.Grow;
import game.plants.Growing;
import game.plants.Plant;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

/**
 * The first growth stage of a strawberry plant.
 * <p>
 * Every {@value #GROW_INTERVAL} turns it has a {@value #GROW_CHANCE_PERCENT}%
 * chance of growing into a {@link StrawberrySprout}.
 * <p>
 * A seedling is a {@link Plant} but not a crop: it cannot be watered or
 * harvested, and the Farmer can walk onto it. It has no earlier stage, so
 * when it is regressed it dies, leaving Dirt.
 */
public class StrawberrySeedling extends Plant implements Growing {

    /** How many turns between attempts to grow. */
    private static final int GROW_INTERVAL = 5;

    /** The chance, as a percentage, of growing on its turn. */
    private static final int GROW_CHANCE_PERCENT = 50;

    /** The randomness this plant hands to its next stage. */
    private final RandomUtils randomUtils;

    /**
     * Builds Areas for the next stage; this seedling does not use it itself
     * (it has no spreading ability of its own), only forwards it, since
     * {@link StrawberrySprout} needs one for its own spreading.
     */
    private final AreaFactory areaFactory;

    /**
     * Creates a new Strawberry Seedling with an age of 0.
     *
     * @param randomUtils the randomness used to roll this plant's chances,
     *                    and handed to the stage it grows into
     * @param areaFactory builds Areas for the stage this plant grows into
     */
    public StrawberrySeedling(RandomUtils randomUtils, AreaFactory areaFactory) {
        super('\'', "Strawberry Seedling",
                new Grow(GROW_INTERVAL, GROW_CHANCE_PERCENT, randomUtils));
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * A Strawberry Seedling grows into a new Strawberry Sprout.
     *
     * @return a new StrawberrySprout
     */
    @Override
    public Plant nextStage() {
        return new StrawberrySprout(randomUtils, areaFactory);
    }
}
