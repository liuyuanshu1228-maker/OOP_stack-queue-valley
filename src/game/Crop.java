package game;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Abstract base class for all crops.
 * <p>
 * A crop is a type of {@link Ground} that keeps track of how many times it
 * has been watered, since several crop-specific rules (chance of damage,
 * amount of produce, etc.) depend on that count. When a crop is removed from
 * the game, for any reason, its tile reverts to {@link Dirt}.
 */
public abstract class Crop extends Ground {

    /**
     * The number of times this crop has been watered.
     */
    private int wateringCount = 0;

    /**
     * Constructor.
     *
     * @param displayChar character to display for this crop
     * @param name the name of this crop
     */
    public Crop(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * How many times this crop has been watered so far.
     *
     * @return the number of times this crop has been watered
     */
    public int getWateringCount() {
        return wateringCount;
    }

    /**
     * Records that this crop has been watered once.
     */
    protected void recordWatering() {
        wateringCount++;
    }

    /**
     * Removes this crop from the ground, turning its tile back into dirt.
     *
     * @param location the location currently occupied by this crop
     */
    protected void removeCrop(Location location) {
        location.setGround(new Dirt());
    }
}
