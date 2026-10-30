package game.wanderer;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Wraps the Wanderer's ordinary wandering Action for a turn when the current
 * weather could not be read (no API key, no network, or the request
 * failed), so the Farmer is told why no weather effect happened, as well as
 * seeing the Wanderer move as usual.
 */
public class UnreadableSkyAction extends Action {

    /** The message shown before the wandering Action's own description. */
    private static final String MESSAGE = "The sky is unreadable, and the Wanderer only wanders.";

    /** The ordinary wandering Action this turn would otherwise perform. */
    private final Action wandering;

    /**
     * Creates an Action that reports the unreadable sky, then wanders.
     *
     * @param wandering the ordinary wandering Action for this turn
     */
    public UnreadableSkyAction(Action wandering) {
        this.wandering = wandering;
    }

    /**
     * Reports the unreadable sky, then performs the wandering Action.
     *
     * @param actor the Wanderer performing this Action
     * @param map the map the Wanderer is on
     * @return the unreadable-sky message followed by the wandering outcome
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return MESSAGE + " " + wandering.execute(actor, map);
    }

    /**
     * Describes this Action for the menu, exactly as the wrapped wandering
     * Action would.
     *
     * @param actor the actor that would perform the Action
     * @return the wandering Action's own menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return wandering.menuDescription(actor);
    }
}
