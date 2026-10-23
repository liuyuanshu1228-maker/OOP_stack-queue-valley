package game.plants;

/**
 * The only early growth stage of a cauliflower plant, before the mature
 * Cauliflower crop.
 * <p>
 * A sprout is a {@link Plant} but not a crop: it cannot be watered or
 * harvested, and the Farmer can walk onto it. It has no earlier stage, so
 * when it is regressed it dies back to Soil.
 */
public class CauliflowerSprout extends Plant {

    /**
     * Creates a new Cauliflower Sprout with an age of 0.
     */
    public CauliflowerSprout() {
        super('y', "Cauliflower Sprout");
    }
}
