package game.crops;

import game.plants.Grow;
import game.plants.Plant;

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
public class CauliflowerSprout extends Plant {

    /** How many turns between attempts to grow. */
    private static final int GROW_INTERVAL = 15;

    /** The chance, as a percentage, of growing on its turn. */
    private static final int GROW_CHANCE_PERCENT = 25;

    /**
     * Creates a new Cauliflower Sprout with an age of 0.
     */
    public CauliflowerSprout() {
        super('y', "Cauliflower Sprout",
                new Grow(GROW_INTERVAL, GROW_CHANCE_PERCENT));
    }

    /**
     * A Cauliflower Sprout grows into a new Cauliflower.
     *
     * @return a new Cauliflower
     */
    @Override
    protected Plant nextStage() {
        return new Cauliflower();
    }
}
