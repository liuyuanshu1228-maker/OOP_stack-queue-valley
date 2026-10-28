package game.crops;

import game.plants.Grow;
import game.plants.Plant;
import game.plants.SelfReplicate;

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
public class MelonSprout extends Plant {

    /** How many turns between attempts to grow. */
    private static final int GROW_INTERVAL = 15;

    /** The chance, as a percentage, of growing on its turn. */
    private static final int GROW_CHANCE_PERCENT = 25;

    /** How many turns between plantings of a sprout nearby. */
    private static final int SPREAD_INTERVAL = 10;

    /**
     * Creates a new Melon Sprout with an age of 0.
     */
    public MelonSprout() {
        super('t', "Melon Sprout",
                new Grow(GROW_INTERVAL, GROW_CHANCE_PERCENT, Melon::new),
                new SelfReplicate(SPREAD_INTERVAL, MelonSprout::new));
    }
}
