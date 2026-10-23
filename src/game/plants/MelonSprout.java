package game.plants;

/**
 * The only early growth stage of a melon plant, before the mature Melon crop.
 * <p>
 * A sprout is a {@link Plant} but not a crop: it cannot be watered or
 * harvested, and the Farmer can walk onto it. It has no earlier stage, so
 * when it is regressed it dies back to Soil.
 */
public class MelonSprout extends Plant {

    /**
     * Creates a new Melon Sprout with an age of 0.
     */
    public MelonSprout() {
        super('t', "Melon Sprout");
    }
}
