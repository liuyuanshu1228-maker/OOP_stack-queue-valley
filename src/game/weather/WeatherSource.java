package game.weather;

import java.util.Optional;

/**
 * Somewhere the game can ask for the real weather at a place on Earth.
 * <p>
 * The game depends only on this interface. The real implementation calls a
 * weather API over the network; tests use one that returns fixed readings, so
 * they never need the network or an API key.
 */
public interface WeatherSource {

    /**
     * Finds the weather at a place right now.
     *
     * @param latitude the latitude of the place, from -90 to 90
     * @param longitude the longitude of the place, from -180 to 180
     * @return the current weather, or empty if it could not be found, for
     * example because there is no network or no API key
     */
    public Optional<WeatherReading> currentWeatherAt(double latitude, double longitude);

    /**
     * Finds the weather forecast for a place at about this time tomorrow.
     *
     * @param latitude the latitude of the place, from -90 to 90
     * @param longitude the longitude of the place, from -180 to 180
     * @return tomorrow's forecast, or empty if it could not be found, for
     * example because there is no network or no API key
     */
    public Optional<WeatherReading> tomorrowWeatherAt(double latitude, double longitude);
}
