package game.wanderer;

import game.positions.area.AreaFactory;
import game.random.RandomUtils;
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
    /** A condition name that, on its own, triggers a frost omen regardless of temperature. */
    private static final String SNOW_CONDITION = "Snow";
    /** Forecast temperature, in Celsius, at or below which a frost omen is triggered. */
    private static final double FROST_THRESHOLD_CELSIUS = 2.0;
    /** The condition name a drought omen requires, together with the heat threshold. */
    private static final String CLEAR_CONDITION = "Clear";
    /** Forecast temperature, in Celsius, at or above which a drought omen is triggered. */
    private static final double DROUGHT_TEMPERATURE_THRESHOLD_CELSIUS = 25.0;

    /** Builds the Area each produced WeatherReaction or ForecastOmen affects. */
    private final AreaFactory areaFactory;
    /** The randomness handed to each Strawberry Seedling a SowingOmen sows. */
    private final RandomUtils randomUtils;

    /**
     * Creates a weather interpreter.
     *
     * @param areaFactory builds the Area each produced WeatherReaction or ForecastOmen affects
     * @param randomUtils the randomness handed to each Strawberry Seedling a SowingOmen sows
     */
    public WeatherInterpreter(AreaFactory areaFactory, RandomUtils randomUtils) {
        this.areaFactory = areaFactory;
        this.randomUtils = randomUtils;
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
            return Optional.of(new CampfireReaction(reading, areaFactory, COLD_THRESHOLD_CELSIUS));
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
        if (isFrosty(forecast)) {
            return Optional.of(new FrostOmen(forecast, areaFactory, FROST_THRESHOLD_CELSIUS));
        }
        if (isRainy(forecast)) {
            return Optional.of(new SowingOmen(forecast, areaFactory, randomUtils));
        }
        if (isDrought(forecast)) {
            return Optional.of(new DroughtOmen(forecast));
        }
        return Optional.empty();
    }

    /**
     * Decides whether a forecast counts as frosty: snow, or cold enough on
     * its own.
     *
     * @param forecast the forecast reading to check
     * @return true if the condition is Snow or the temperature is at most the frost threshold
     */
    private boolean isFrosty(WeatherReading forecast) {
        return SNOW_CONDITION.equals(forecast.getCondition())
                || forecast.getTemperatureCelsius() <= FROST_THRESHOLD_CELSIUS;
    }

    /**
     * Decides whether a forecast counts as a drought: clear and hot.
     *
     * @param forecast the forecast reading to check
     * @return true if the condition is Clear and the temperature is at least the drought threshold
     */
    private boolean isDrought(WeatherReading forecast) {
        return CLEAR_CONDITION.equals(forecast.getCondition())
                && forecast.getTemperatureCelsius() >= DROUGHT_TEMPERATURE_THRESHOLD_CELSIUS;
    }
}
