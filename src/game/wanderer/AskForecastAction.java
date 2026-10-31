package game.wanderer;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.weather.MapGeography;
import game.weather.WeatherReading;
import game.weather.WeatherSource;

import java.util.Optional;

/**
 * The Action an actor performs when it asks a Wanderer for tomorrow's
 * forecast. It depends only on {@link ForecastOmen} and {@link WeatherSource},
 * so this class never names a concrete omen itself.
 */
public class AskForecastAction extends Action {

    /** Shown when tomorrow's weather at the Wanderer's Location could not be read. */
    private static final String UNREADABLE_FORECAST_MESSAGE =
            "The Wanderer squints at the sky, but tomorrow's weather is unreadable.";
    /** Shown when a forecast was read but no omen applies to it. */
    private static final String NOTHING_UNUSUAL_MESSAGE =
            "The Wanderer shrugs: tomorrow looks like an ordinary day.";

    /** The Wanderer being asked; its own Location is where the omen takes effect. */
    private final Wanderer wanderer;
    /** Reports tomorrow's weather at a given latitude and longitude. */
    private final WeatherSource weatherSource;
    /** Converts the Wanderer's Location into a latitude and longitude. */
    private final MapGeography mapGeography;
    /** Decides which omen, if any, a forecast reading triggers. */
    private final WeatherInterpreter weatherInterpreter;

    /**
     * Creates an action that asks the given Wanderer for tomorrow's forecast.
     *
     * @param wanderer the Wanderer being asked
     * @param weatherSource reports tomorrow's weather at a given latitude and longitude
     * @param mapGeography converts the Wanderer's Location into a latitude and longitude
     * @param weatherInterpreter decides which omen, if any, a forecast reading triggers
     */
    public AskForecastAction(Wanderer wanderer, WeatherSource weatherSource,
                             MapGeography mapGeography, WeatherInterpreter weatherInterpreter) {
        this.wanderer = wanderer;
        this.weatherSource = weatherSource;
        this.mapGeography = mapGeography;
        this.weatherInterpreter = weatherInterpreter;
    }

    /**
     * Looks up tomorrow's weather at the Wanderer's Location and, if an omen
     * applies, brings it upon the asking actor; otherwise reports why not.
     *
     * @param actor the actor asking for the forecast
     * @param map the map the actor is on
     * @return a description of what the forecast revealed
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location here = map.locationOf(wanderer);
        Optional<WeatherReading> forecast = weatherSource.tomorrowWeatherAt(
                mapGeography.latitudeOf(here), mapGeography.longitudeOf(here));
        if (forecast.isEmpty()) {
            return UNREADABLE_FORECAST_MESSAGE;
        }

        Optional<ForecastOmen> omen = weatherInterpreter.omenFor(forecast.get());
        if (omen.isEmpty()) {
            return NOTHING_UNUSUAL_MESSAGE;
        }
        return omen.get().bringUpon(actor, here);
    }

    /**
     * Describes this Action for the menu.
     *
     * @param actor the actor that would perform the Action
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " asks " + wanderer + " about tomorrow's weather";
    }
}
