package game.pests;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * A pest feeding on the plant it stands on.
 * <p>
 * It depends only on {@link Infestation}, so every pest uses this one action,
 * and what feeding does is up to the pest. The description it returns is shown
 * by the engine, so the player sees what the pest did.
 * <p>
 * It lives in the pests package, not in game.actions, because {@link Pest}
 * creates it; keeping both here avoids a package cycle.
 */
public class InfestAction extends Action {

    /** The pest that feeds. */
    private final Infestation infestation;

    /**
     * Creates the action.
     *
     * @param infestation the pest that feeds
     */
    public InfestAction(Infestation infestation) {
        this.infestation = infestation;
    }

    /**
     * Lets the pest feed on the tile it stands on.
     *
     * @param actor the pest
     * @param map the map containing the pest
     * @return a description of what the pest did
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return infestation.feedOn(map.locationOf(actor));
    }

    /**
     * @param actor the pest
     * @return a description of this action for a menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " feeds on the plant here";
    }
}
