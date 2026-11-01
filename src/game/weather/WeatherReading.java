package game.weather;

import java.util.Locale;

/**
 * The weather at one place and time, as reported by a {@link WeatherSource}.
 * <p>
 * It keeps only the values the game uses: the kind of weather (for example
 * "Rain" or "Clear"), the temperature, the wind speed and the rainfall. The
 * values are checked when the reading is made, so code that uses a reading
 * never has to check them again.
 */
public class WeatherReading {

    /** The kind of weather, for example "Rain", "Snow" or "Clear". */
    private final String condition;

    /** The air temperature, in degrees Celsius. */
    private final double temperatureCelsius;

    /** The wind speed, in metres per second. */
    private final double windSpeedMetresPerSecond;

    /** The rainfall over the last hour, in millimetres. */
    private final double rainMillimetres;

    /**
     * Creates a weather reading.
     *
     * @param condition the kind of weather, for example "Rain"
     * @param temperatureCelsius the air temperature, in degrees Celsius
     * @param windSpeedMetresPerSecond the wind speed, in metres per second
     * @param rainMillimetres the rainfall over the last hour, in millimetres
     * @throws IllegalArgumentException if the condition is blank, a value is not
     *                                  a finite number, or the wind or rain is negative
     */
    public WeatherReading(String condition, double temperatureCelsius,
                          double windSpeedMetresPerSecond, double rainMillimetres) {
        if (condition == null || condition.isBlank()) {
            throw new IllegalArgumentException("A weather reading needs a condition");
        }
        if (!Double.isFinite(temperatureCelsius)) {
            throw new IllegalArgumentException("The temperature must be a finite number");
        }
        requireFiniteAndNotNegative("wind speed", windSpeedMetresPerSecond);
        requireFiniteAndNotNegative("rainfall", rainMillimetres);
        this.condition = condition;
        this.temperatureCelsius = temperatureCelsius;
        this.windSpeedMetresPerSecond = windSpeedMetresPerSecond;
        this.rainMillimetres = rainMillimetres;
    }

    /**
     * Returns the kind of weather.
     *
     * @return the kind of weather, for example "Rain"
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Returns the air temperature.
     *
     * @return the air temperature, in degrees Celsius
     */
    public double getTemperatureCelsius() {
        return temperatureCelsius;
    }

    /**
     * Returns the wind speed.
     *
     * @return the wind speed, in metres per second
     */
    public double getWindSpeedMetresPerSecond() {
        return windSpeedMetresPerSecond;
    }

    /**
     * Returns the rainfall over the last hour.
     *
     * @return the rainfall over the last hour, in millimetres
     */
    public double getRainMillimetres() {
        return rainMillimetres;
    }

    /**
     * Describes this reading for the game's messages.
     *
     * @return for example "Rain, 12.3°C, wind 6.2 m/s, rain 2.1 mm"
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s, %.1f°C, wind %.1f m/s, rain %.1f mm",
                condition, temperatureCelsius, windSpeedMetresPerSecond, rainMillimetres);
    }

    /**
     * Rejects a measurement that is negative, infinite or not a number.
     *
     * @param name what the value measures, used in the error message
     * @param value the value to check
     * @throws IllegalArgumentException if the value is negative, infinite or not a number
     */
    private void requireFiniteAndNotNegative(String name, double value) {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException("The " + name + " must be finite and not negative, but was " + value);
        }
    }
}
