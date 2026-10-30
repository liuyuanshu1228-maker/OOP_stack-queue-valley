package game.wanderer;

import game.positions.area.AreaFactory;
import game.weather.WeatherReading;

import java.util.Optional;

/**
 * Decides which {@link WeatherReaction}, if any, a current weather reading
 * triggers, so that no other class needs to name a concrete reaction.
 * Checks the agreed thresholds in a fixed order and stops at the first one
 * that matches: a strong wind first, then rain, then cold.
 * <p>
 * This class deliberately covers only the current-weather side of REQ5.
 * The forecast/omen side (choosing a {@link ForecastOmen} from a forecast
 * reading) belongs in this same class, since it is the one class that names
 * every concrete reaction and omen; it has not been added here so as not to
 * clash with that part of the feature while it is written separately.
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
}
