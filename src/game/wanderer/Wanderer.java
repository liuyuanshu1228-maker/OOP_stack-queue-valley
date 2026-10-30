package game.wanderer;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.WanderingActor;
import game.behaviours.WanderBehaviour;
import game.random.RandomUtils;
import game.weather.MapGeography;
import game.weather.WeatherReading;
import game.weather.WeatherSource;

import java.util.Optional;

/**
 * An actor whose behaviour depends on the real weather at the place the map
 * represents. Every {@value #REACTION_INTERVAL_TURNS} turns it asks for the
 * current weather at its own Location and, if a {@link WeatherReaction}
 * applies, performs it; otherwise, exactly like any other
 * {@link WanderingActor}, it wanders.
 * <p>
 * This class never names a concrete WeatherReaction itself: deciding which
 * one applies, if any, is entirely the {@link WeatherInterpreter}'s job.
 */
public class Wanderer extends WanderingActor {

    /** The name of this actor. */
    private static final String NAME = "Wanderer";
    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = 'W';
    /** This actor's starting hit points. */
    private static final int HIT_POINTS = 50;
    /** How many turns pass between one weather check and the next. */
    private static final int REACTION_INTERVAL_TURNS = 5;

    /** Reports the current weather at a given latitude and longitude. */
    private final WeatherSource weatherSource;
    /** Converts this actor's Location into a latitude and longitude. */
    private final MapGeography mapGeography;
    /** Decides which reaction, if any, a weather reading triggers. */
    private final WeatherInterpreter weatherInterpreter;
    /** Turns elapsed since the last weather check. */
    private int turnsSinceLastReaction;

    /**
     * Creates a Wanderer.
     *
     * @param randomUtils the randomness used to build this actor's fallback
     *                    wandering behaviour
     * @param weatherSource reports the current weather at a given latitude and longitude
     * @param mapGeography converts this actor's Location into a latitude and longitude
     * @param weatherInterpreter decides which reaction, if any, a weather reading triggers
     */
    public Wanderer(RandomUtils randomUtils, WeatherSource weatherSource,
                    MapGeography mapGeography, WeatherInterpreter weatherInterpreter) {
        super(NAME, DISPLAY_CHAR, HIT_POINTS, new WanderBehaviour(randomUtils));
        this.weatherSource = weatherSource;
        this.mapGeography = mapGeography;
        this.weatherInterpreter = weatherInterpreter;
        this.turnsSinceLastReaction = 0;
    }

    /**
     * Reacts to the weather if it is time to check it and a reaction
     * applies; otherwise wanders, exactly as a WanderingActor normally does.
     *
     * @param actions collection of possible Actions for this actor
     * @param lastAction the Action this actor took last turn
     * @param map the map containing this actor
     * @param display the I/O object to which messages may be written
     * @return the Action to be performed
     */
    @Override
    protected Action playConsciousTurn(ActionList actions, Action lastAction,
                                       GameMap map, Display display) {
        turnsSinceLastReaction++;
        if (turnsSinceLastReaction < REACTION_INTERVAL_TURNS) {
            return super.playConsciousTurn(actions, lastAction, map, display);
        }
        turnsSinceLastReaction = 0;

        Optional<WeatherReaction> reaction = currentReaction(map.locationOf(this));
        if (reaction.isEmpty()) {
            return super.playConsciousTurn(actions, lastAction, map, display);
        }
        return new ReactToWeatherAction(reaction.get());
    }

    /**
     * Asks for the current weather at the given Location and, if a reading
     * comes back, asks the WeatherInterpreter which reaction it triggers.
     *
     * @param here this actor's current Location
     * @return the reaction that applies, or empty if there is no reading or
     * no reaction applies to it
     */
    private Optional<WeatherReaction> currentReaction(Location here) {
        Optional<WeatherReading> reading = weatherSource.currentWeatherAt(
                mapGeography.latitudeOf(here), mapGeography.longitudeOf(here));
        if (reading.isEmpty()) {
            return Optional.empty();
        }
        return weatherInterpreter.reactionFor(reading.get());
    }
}
