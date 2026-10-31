package game.wanderer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.weather.WeatherReading;

/**
 * The omen of a hot, dry forecast: drains the asking actor's stamina over the
 * following turns, via {@link DroughtWearinessStatus}.
 * <p>
 * Unlike {@link FrostOmen} and {@link BountyOmen}, this omen affects only the
 * actor who asked, not an area, so it needs no {@code AreaFactory}.
 */
public class DroughtOmen implements ForecastOmen {

    /** Tomorrow's forecast reading that triggered this omen. */
    private final WeatherReading forecast;

    /**
     * Creates a drought omen.
     *
     * @param forecast tomorrow's forecast reading that triggered this omen
     */
    public DroughtOmen(WeatherReading forecast) {
        this.forecast = forecast;
    }

    /**
     * Attaches a DroughtWearinessStatus to the asking actor.
     *
     * @param asker the actor who asked for the forecast
     * @param here the tile the Wanderer stands on
     * @return a description of the drought warning, including {@code here}
     */
    @Override
    public String bringUpon(Actor asker, Location here) {
        asker.addStatus(new DroughtWearinessStatus(asker));
        return "The Wanderer, standing at " + here + ", warns " + asker + " of a "
                + forecast.getTemperatureCelsius() + "°C, " + forecast.getRainMillimetres()
                + " mm/h drought tomorrow: " + asker + " already feels weary at the thought.";
    }
}
