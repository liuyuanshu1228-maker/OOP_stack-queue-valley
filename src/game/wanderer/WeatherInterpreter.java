package game.wanderer;

import game.positions.area.AreaFactory;
import game.weather.WeatherReading;

import java.util.Optional;

/**
 * Decides which {@link WeatherReaction} a current weather reading triggers,
 * and which {@link ForecastOmen} a forecast reading triggers, so that no
 * other class needs to name a concrete reaction or omen. Both sides check
 * their own agreed thresholds in a fixed order and stop at the first one
 * that matches: {@link #reactionFor} tries a strong wind, then rain, then
 * cold; {@link #omenFor} tries frost, then rain, then heat.
 */
public class WeatherInterpreter {

    /** Wind speed, in m/s, at or above which a storm shelter reaction is triggered. */
    private static final double STORM_WIND_THRESHOLD_MPS = 10.0;
    /** Temperature, in Celsius, at or below which a campfire reaction is triggered. */
    private static final double COLD_THRESHOLD_CELSIUS = 10.0;
    /** A condition name that counts as rainy. */
    private static final String RAIN_CONDITION = "Rain";
    /** A condition name that counts as rainy. */
    private static final String DRIZZLE_CONDITION = "Drizzle";
    /** A condition name that counts as rainy. */
    private static final String THUNDERSTORM_CONDITION = "Thunderstorm";
    /** Forecast temperature, in Celsius, at or below which a frost omen is triggered. */
    private static final double FROST_THRESHOLD_CELSIUS = 5.0;
    /** Forecast temperature, in Celsius, at or above which a drought omen is triggered. */
    private static final double DROUGHT_TEMPERATURE_THRESHOLD_CELSIUS = 25.0;
    /** Forecast rainfall, in mm/h, at or below which a day counts as dry enough for drought. */
    private static final double DROUGHT_RAIN_THRESHOLD_MM = 0.5;

    /** Builds the Area each produced WeatherReaction affects. */
    private final AreaFactory areaFactory;

    /**
     * Creates a weather interpreter.
     *
     * @param areaFactory builds the Area each produced WeatherReaction affects
     */
    public WeatherInterpreter(AreaFactory areaFactory) {
        this.areaFactory = areaFactory;
    }

    /**
     * Decides which reaction, if any, the given reading triggers.
     *
     * @param reading the weather reading to interpret
     * @return the reaction that applies, or empty if none does
     */
    public Optional<WeatherReaction> reactionFor(WeatherReading reading) {
        if (reading.getWindSpeedMetresPerSecond() >= STORM_WIND_THRESHOLD_MPS) {
            return Optional.of(new StormShelterReaction(reading, areaFactory));
        }
        if (isRainy(reading)) {
            return Optional.of(new RainDanceReaction(reading, areaFactory));
        }
        if (reading.getTemperatureCelsius() <= COLD_THRESHOLD_CELSIUS) {
            return Optional.of(new CampfireReaction(reading, areaFactory));
        }
        return Optional.empty();
    }

    /**
     * Decides whether a reading's condition counts as rainy.
     *
     * @param reading the weather reading to check
     * @return true if the condition is Rain, Drizzle or Thunderstorm
     */
    private boolean isRainy(WeatherReading reading) {
        String condition = reading.getCondition();
        return RAIN_CONDITION.equals(condition)
                || DRIZZLE_CONDITION.equals(condition)
                || THUNDERSTORM_CONDITION.equals(condition);
    }

    /**
     * Decides which omen, if any, the given forecast triggers.
     *
     * @param forecast the forecast reading to interpret
     * @return the omen that applies, or empty if none does
     */
    public Optional<ForecastOmen> omenFor(WeatherReading forecast) {
        if (forecast.getTemperatureCelsius() <= FROST_THRESHOLD_CELSIUS) {
            return Optional.of(new FrostOmen(forecast, areaFactory));
        }
        if (isRainy(forecast)) {
            return Optional.of(new BountyOmen(forecast, areaFactory));
        }
        if (isDrought(forecast)) {
            return Optional.of(new DroughtOmen(forecast));
        }
        return Optional.empty();
    }

    /**
     * Decides whether a forecast counts as a drought: hot and effectively dry.
     *
     * @param forecast the forecast reading to check
     * @return true if the temperature is at least the drought threshold and
     * the rainfall is at most the dry threshold
     */
    private boolean isDrought(WeatherReading forecast) {
        return forecast.getTemperatureCelsius() >= DROUGHT_TEMPERATURE_THRESHOLD_CELSIUS
                && forecast.getRainMillimetres() <= DROUGHT_RAIN_THRESHOLD_MM;
    }
}
