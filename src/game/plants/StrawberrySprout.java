package game.plants;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * The second growth stage of a strawberry plant, between
 * {@link StrawberrySeedling} and the mature Strawberry crop.
 * <p>
 * A sprout is a {@link Plant} but not a crop: it cannot be watered or
 * harvested, and the Farmer can walk onto it. When it is regressed it goes
 * back to being a {@link StrawberrySeedling}.
 */
public class StrawberrySprout extends Plant {

    /**
     * Creates a new Strawberry Sprout with an age of 0.
     */
    public StrawberrySprout() {
        super('v', "Strawberry Sprout");
    }

    /**
     * A regressed Strawberry Sprout becomes a new Strawberry Seedling.
     *
     * @return a new StrawberrySeedling
     */
    @Override
    protected Ground previousStage() {
        return new StrawberrySeedling();
    }
}
