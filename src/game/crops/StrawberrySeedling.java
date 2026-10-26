package game.crops;

import game.plants.Grow;
import game.plants.Plant;

/**
 * The first growth stage of a strawberry plant.
 * <p>
 * Every {@value #GROW_INTERVAL} turns it has a {@value #GROW_CHANCE_PERCENT}%
 * chance of growing into a {@link StrawberrySprout}.
 * <p>
 * A seedling is a {@link Plant} but not a crop: it cannot be watered or
 * harvested, and the Farmer can walk onto it. It has no earlier stage, so
 * when it is regressed it dies back to Soil.
 */
public class StrawberrySeedling extends Plant {

    /** How many turns between attempts to grow. */
    private static final int GROW_INTERVAL = 5;

    /** The chance, as a percentage, of growing on its turn. */
    private static final int GROW_CHANCE_PERCENT = 50;

    /**
     * Creates a new Strawberry Seedling with an age of 0.
     */
    public StrawberrySeedling() {
        super('\'', "Strawberry Seedling",
                new Grow(GROW_INTERVAL, GROW_CHANCE_PERCENT, StrawberrySprout::new));
    }
}
