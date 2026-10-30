package game.wanderer;

import edu.monash.fit2099.engine.positions.Location;
import game.positions.FreelyWaterable;
import game.positions.Tillable;
import game.positions.area.AreaFactory;
import game.weather.WeatherReading;

/**
 * The reaction to rain: over an area around the given Location that grows
 * with the rainfall in the reading that triggered it, turns bare, tillable
 * ground to Soil and gives every freely-waterable crop a free watering.
 * <p>
 * Reuses the existing {@link Tillable} and {@link FreelyWaterable} capability
 * interfaces rather than naming Dirt or any crop directly, so this class
 * needs no {@code instanceof} and works for any ground that offers either
 * capability.
 */
public class RainDanceReaction implements WeatherReaction {

    /** The area always reaches at least this many tiles, even in light rain. */
    private static final int MIN_RADIUS_TILES = 1;

    /** The weather reading that triggered this reaction. */
    private final WeatherReading reading;
    /** Builds the Area of tiles this reaction affects. */
    private final AreaFactory areaFactory;

    /**
     * Creates a rain dance reaction.
     *
     * @param reading the weather reading that triggered this reaction
     * @param areaFactory builds the Area of tiles this reaction affects
     */
    public RainDanceReaction(WeatherReading reading, AreaFactory areaFactory) {
        this.reading = reading;
        this.areaFactory = areaFactory;
    }

    /**
     * Tills every tillable tile and waters every freely-waterable crop within
     * a radius of {@code here} that grows with the rainfall.
     *
     * @param here the tile the Wanderer stands on
     * @return a description of what the rain did, including {@code here}
     */
    @Override
    public String reactAround(Location here) {
        int radius = Math.max(MIN_RADIUS_TILES, (int) Math.ceil(reading.getRainMillimetres()));
        int tilledTiles = 0;
        int wateredCrops = 0;
        for (Location tile : areaFactory.within(here, radius).tiles()) {
            Tillable tillable = tile.getGroundAs(Tillable.class);
            if (tillable != null) {
                tile.setGround(tillable.getTillResult());
                tilledTiles++;
            }
            FreelyWaterable waterable = tile.getGroundAs(FreelyWaterable.class);
            if (waterable != null) {
                waterable.receiveFreeWatering();
                wateredCrops++;
            }
        }
        return "The Wanderer dances for rain around " + here + ": " + tilledTiles
                + " bare tiles turn to Soil, and " + wateredCrops + " crops receive a free watering.";
    }
}
