package game.wanderer;

import edu.monash.fit2099.engine.positions.Location;
import game.items.Fire;
import game.positions.area.AreaFactory;
import game.positions.area.TileListDescription;
import game.weather.WeatherReading;

import java.util.ArrayList;
import java.util.List;

/**
 * The reaction to cold weather: lights a {@link Fire} on tiles adjacent to
 * the given Location, with more tiles catching alight the colder the
 * temperature in the reading that triggered it.
 */
public class CampfireReaction implements WeatherReaction {

    /** At least this many adjacent tiles catch alight, however mild the cold. */
    private static final int MIN_TILES_ALIGHT = 1;
    /** No more than this many adjacent tiles ever catch alight. */
    private static final int MAX_TILES_ALIGHT = 8;
    /** How many turns each lit Fire burns before going out. */
    private static final int FIRE_LIFETIME_TURNS = 4;
    /** Damage per turn each lit Fire's burn inflicts. */
    private static final int BURN_DAMAGE_PER_TURN = 5;
    /** How many turns each lit Fire's burn lasts on its victim. */
    private static final int BURN_DURATION_TURNS = 3;

    /** The weather reading that triggered this reaction. */
    private final WeatherReading reading;
    /** Builds the Area of tiles this reaction affects. */
    private final AreaFactory areaFactory;
    /**
     * The temperature, in Celsius, below which this reaction is triggered --
     * the same value {@link WeatherInterpreter} already checked before
     * constructing this reaction, passed in rather than repeated here as a
     * second, independent constant.
     */
    private final double coldThresholdCelsius;

    /**
     * Creates a campfire reaction.
     *
     * @param reading the weather reading that triggered this reaction
     * @param areaFactory builds the Area of tiles this reaction affects
     * @param coldThresholdCelsius the temperature, in Celsius, below which
     *                             this reaction is triggered, as decided by
     *                             {@link WeatherInterpreter}
     */
    public CampfireReaction(WeatherReading reading, AreaFactory areaFactory, double coldThresholdCelsius) {
        this.reading = reading;
        this.areaFactory = areaFactory;
        this.coldThresholdCelsius = coldThresholdCelsius;
    }

    /**
     * Lights a Fire on a number of tiles adjacent to {@code here} that grows
     * as the temperature drops.
     *
     * @param here the tile the Wanderer stands on
     * @return a description of how many tiles caught alight, including {@code here}
     */
    @Override
    public String reactAround(Location here) {
        int tilesToLight = Math.min(MAX_TILES_ALIGHT,
                Math.max(MIN_TILES_ALIGHT, (int) (coldThresholdCelsius - reading.getTemperatureCelsius())));
        List<Location> tilesLit = new ArrayList<>();
        for (Location tile : areaFactory.adjacentTo(here).tiles()) {
            if (tilesLit.size() >= tilesToLight) {
                break;
            }
            tile.addItem(new Fire(FIRE_LIFETIME_TURNS, BURN_DAMAGE_PER_TURN, BURN_DURATION_TURNS));
            tilesLit.add(tile);
        }
        return "The Wanderer lights a campfire against the " + reading.getTemperatureCelsius()
                + "°C cold around " + here + ": " + tilesLit.size() + " tiles catch alight at "
                + new TileListDescription().of(tilesLit) + ".";
    }
}
