package game.plants;

/**
 * The first growth stage of a strawberry plant.
 * <p>
 * A seedling is a {@link Plant} but not a crop: it cannot be watered or
 * harvested, and the Farmer can walk onto it. It has no earlier stage, so
 * when it is regressed it dies back to Soil.
 */
public class StrawberrySeedling extends Plant {

    /**
     * Creates a new Strawberry Seedling with an age of 0.
     */
    public StrawberrySeedling() {
        super('\'', "Strawberry Seedling");
    }
}
