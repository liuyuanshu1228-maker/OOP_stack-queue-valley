package game.wanderer;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * The Action an actor performs when it reacts to the weather around it. It
 * delegates the actual effect to whichever {@link WeatherReaction} it was
 * given, so this class never names a concrete reaction itself.
 */
public class ReactToWeatherAction extends Action {

    /** The reaction this Action performs. */
    private final WeatherReaction reaction;

    /**
     * Creates an Action that performs the given reaction.
     *
     * @param reaction the reaction to perform when this Action executes
     */
    public ReactToWeatherAction(WeatherReaction reaction) {
        this.reaction = reaction;
    }

    /**
     * Performs the reaction around the acting actor's current Location.
     *
     * @param actor the actor reacting to the weather
     * @param map the map the actor is on
     * @return a description of what the reaction did
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return reaction.reactAround(map.locationOf(actor));
    }

    /**
     * Describes this Action for the menu.
     *
     * @param actor the actor that would perform the Action
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " reacts to the weather";
    }
}
