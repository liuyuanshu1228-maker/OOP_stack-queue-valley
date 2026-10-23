package game.plants;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.farm.Soil;

/**
 * Abstract base class for every plant, at any growth stage.
 * <p>
 * A plant is a type of {@link Ground} with its own age: it is 0 when the plant
 * is placed on the map and goes up by 1 every turn. Only the plant itself
 * changes its age, so there is no setter.
 * <p>
 * Every plant is {@link Regressible}. By default a regressed plant dies back
 * to tilled {@link Soil}; plants with an earlier growth stage override
 * {@link #previousStage()} to return it instead.
 */
public abstract class Plant extends Ground implements Regressible {

    /**
     * The number of turns this plant has been on the map.
     */
    private int age = 0;

    /**
     * Creates a new Plant with an age of 0.
     *
     * @param displayChar character to display for this plant
     * @param name the name of this plant
     */
    protected Plant(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * How many turns this plant has been on the map.
     *
     * @return the age of this plant, in turns
     */
    public int getAge() {
        return age;
    }

    /**
     * Ages this plant by one turn.
     *
     * @param location the location of this plant
     */
    @Override
    public void tick(Location location) {
        age++;
    }

    /**
     * The ground this plant falls back to when it is regressed. Defaults to
     * tilled Soil, meaning the plant dies; override for plants that have an
     * earlier growth stage.
     *
     * @return a new instance of the earlier stage
     */
    protected Ground previousStage() {
        return new Soil();
    }

    /**
     * Replaces this plant with its earlier growth stage.
     *
     * @param location the location of this plant
     * @return a description of what happened, to be displayed to the player
     */
    @Override
    public String regress(Location location) {
        Ground previous = previousStage();
        location.setGround(previous);
        return this + " at " + location + " is set back to " + previous + ".";
    }
}
