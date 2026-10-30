package game.wanderer;

import edu.monash.fit2099.engine.positions.Location;
import game.positions.area.AreaFactory;
import game.weather.WeatherReading;

/**
 * The reaction to a strong wind: builds a {@link StormShelterGround} on every
 * tile adjacent to the given Location, blocking them for a duration that
 * grows with the wind speed in the reading that triggered it.
 */
public class StormShelterReaction implements WeatherReaction {

    /** The minimum number of turns the shelter lasts, regardless of wind speed. */
    private static final int BASE_DURATION_TURNS = 3;

    /** The weather reading that triggered this reaction. */
    private final WeatherReading reading;
    /** Builds the Area of tiles this reaction shelters. */
    private final AreaFactory areaFactory;

    /**
     * Creates a storm shelter reaction.
     *
     * @param reading the weather reading that triggered this reaction
     * @param areaFactory builds the Area of tiles this reaction shelters
     */
    public StormShelterReaction(WeatherReading reading, AreaFactory areaFactory) {
        this.reading = reading;
        this.areaFactory = areaFactory;
    }

    /**
     * Shelters every tile adjacent to {@code here} for a duration that grows
     * with the wind speed.
     *
     * @param here the tile the Wanderer stands on
     * @return a description of the shelter built, including {@code here}
     */
    @Override
    public String reactAround(Location here) {
        int durationTurns = BASE_DURATION_TURNS + (int) reading.getWindSpeedMetresPerSecond();
        int shelteredTiles = 0;
        for (Location tile : areaFactory.adjacentTo(here).tiles()) {
            tile.setGround(new StormShelterGround(tile.getGround(), durationTurns));
            shelteredTiles++;
        }
        return "The Wanderer builds a shelter around " + here + " against a "
                + reading.getWindSpeedMetresPerSecond() + " m/s wind, blocking "
                + shelteredTiles + " tiles for " + durationTurns + " turns.";
    }
}
