package game.wanderer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.plants.Plant;
import game.positions.area.AreaFactory;
import game.status.GrowthPausedStatus;
import game.weather.WeatherReading;

/**
 * The omen of a cold forecast: pauses the growth of every {@link Plant}
 * around the Wanderer for a duration that grows with how far below freezing
 * tomorrow's temperature is.
 * <p>
 * Reuses the existing {@link GrowthPausedStatus} rather than inventing a new
 * one, and finds plants with {@link Location#getGroundAs(Class)} rather than
 * naming any concrete crop, so this class needs no {@code instanceof}.
 */
public class FrostOmen implements ForecastOmen {

    /** The minimum number of turns growth is paused for, however mild the frost. */
    private static final int BASE_PAUSE_TURNS = 2;
    /** How many tiles from the Wanderer this omen reaches. */
    private static final int RADIUS_TILES = 2;

    /** Tomorrow's forecast reading that triggered this omen. */
    private final WeatherReading forecast;
    /** Builds the Area of tiles this omen affects. */
    private final AreaFactory areaFactory;
    /**
     * The temperature, in Celsius, at or below which this omen is triggered
     * -- the same value {@link WeatherInterpreter} already checked before
     * constructing this omen, passed in rather than repeated here as a
     * second, independent constant.
     */
    private final double frostThresholdCelsius;

    /**
     * Creates a frost omen.
     *
     * @param forecast tomorrow's forecast reading that triggered this omen
     * @param areaFactory builds the Area of tiles this omen affects
     * @param frostThresholdCelsius the temperature, in Celsius, at or below
     *                              which this omen is triggered, as decided
     *                              by {@link WeatherInterpreter}
     */
    public FrostOmen(WeatherReading forecast, AreaFactory areaFactory, double frostThresholdCelsius) {
        this.forecast = forecast;
        this.areaFactory = areaFactory;
        this.frostThresholdCelsius = frostThresholdCelsius;
    }

    /**
     * Pauses the growth of every Plant within range of {@code here}, for a
     * duration that grows the colder tomorrow's forecast is.
     *
     * @param asker the actor who asked for the forecast
     * @param here the tile the Wanderer stands on
     * @return a description of the frost warning, including {@code here}
     */
    @Override
    public String bringUpon(Actor asker, Location here) {
        int pauseDurationTurns = BASE_PAUSE_TURNS
                + (int) Math.max(0, frostThresholdCelsius - forecast.getTemperatureCelsius());
        int pausedPlants = 0;
        for (Location tile : areaFactory.within(here, RADIUS_TILES).tiles()) {
            Plant plant = tile.getGroundAs(Plant.class);
            if (plant != null) {
                plant.addStatus(new GrowthPausedStatus(pauseDurationTurns));
                pausedPlants++;
            }
        }
        return "The Wanderer warns " + asker + " of a " + forecast.getTemperatureCelsius()
                + "°C frost tomorrow: " + pausedPlants + " plants around " + here
                + " stop growing for " + pauseDurationTurns + " turns.";
    }
}
