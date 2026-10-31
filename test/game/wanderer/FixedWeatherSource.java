package game.wanderer;

import game.weather.WeatherReading;
import game.weather.WeatherSource;

import java.util.Optional;

/**
 * A {@link WeatherSource} that always returns the same fixed current and
 * tomorrow readings, so {@link Wanderer} and {@link AskForecastAction} can be
 * tested without the network, exactly as {@code OpenWeatherClientTest}'s
 * recording client avoids it for the lower-level API classes. Not giving it a
 * reading at all (the default) stands for an unreadable sky.
 */
class FixedWeatherSource implements WeatherSource {

    /** The reading {@link #currentWeatherAt} returns; unreadable until set. */
    private Optional<WeatherReading> current = Optional.empty();

    /** The reading {@link #tomorrowWeatherAt} returns; unreadable until set. */
    private Optional<WeatherReading> tomorrow = Optional.empty();

    /**
     * Sets the reading every future call to {@link #currentWeatherAt} returns.
     *
     * @param reading the reading to return from now on
     */
    void setCurrent(WeatherReading reading) {
        current = Optional.of(reading);
    }

    /**
     * Sets the reading every future call to {@link #tomorrowWeatherAt} returns.
     *
     * @param reading the reading to return from now on
     */
    void setTomorrow(WeatherReading reading) {
        tomorrow = Optional.of(reading);
    }

    @Override
    public Optional<WeatherReading> currentWeatherAt(double latitude, double longitude) {
        return current;
    }

    @Override
    public Optional<WeatherReading> tomorrowWeatherAt(double latitude, double longitude) {
        return tomorrow;
    }
}
