package game.wanderer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.StrawberrySeedling;
import game.positions.Plantable;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;
import game.weather.WeatherReading;

/**
 * The omen of a rainy forecast: a new {@link StrawberrySeedling} takes root
 * on every bare Soil tile around the Wanderer.
 * <p>
 * Finds bare Soil with {@link Location#getGroundAs(Class)} for the
 * {@link Plantable} capability, exactly as {@code Crop.plantNextTo} already
 * does, rather than naming {@code Soil} directly, so this class needs no
 * {@code instanceof}.
 */
public class SowingOmen implements ForecastOmen {

    /** How many tiles from the Wanderer this omen reaches. */
    private static final int RADIUS_TILES = 2;

    /** Tomorrow's forecast reading that triggered this omen. */
    private final WeatherReading forecast;
    /** Builds the Area of tiles this omen affects. */
    private final AreaFactory areaFactory;
    /** The randomness handed to each Strawberry Seedling sown. */
    private final RandomUtils randomUtils;

    /**
     * Creates a sowing omen.
     *
     * @param forecast tomorrow's forecast reading that triggered this omen
     * @param areaFactory builds the Area of tiles this omen affects, and is
     *                     handed to each seedling sown
     * @param randomUtils the randomness handed to each seedling sown
     */
    public SowingOmen(WeatherReading forecast, AreaFactory areaFactory, RandomUtils randomUtils) {
        this.forecast = forecast;
        this.areaFactory = areaFactory;
        this.randomUtils = randomUtils;
    }

    /**
     * Plants a new Strawberry Seedling on every bare Soil tile within range
     * of {@code here}.
     *
     * @param asker the actor who asked for the forecast
     * @param here the tile the Wanderer stands on
     * @return a description of the seedlings sown, including {@code here}
     */
    @Override
    public String bringUpon(Actor asker, Location here) {
        int seedlingsSown = 0;
        for (Location tile : areaFactory.within(here, RADIUS_TILES).tiles()) {
            Plantable plantable = tile.getGroundAs(Plantable.class);
            if (plantable != null) {
                plantable.plant(new StrawberrySeedling(randomUtils, areaFactory), tile);
                seedlingsSown++;
            }
        }
        return "The Wanderer promises " + asker + " a " + forecast.getCondition()
                + " day tomorrow: " + seedlingsSown + " Strawberry Seedlings take root on the bare Soil around "
                + here + ".";
    }
}
