package game.pestcontrol;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An actor eats a pest next to it, which removes the pest from the map.
 */
class EatPestAction extends Action {

    /** The pest to eat. */
    private final Actor pest;

    /**
     * Creates an action that eats a pest.
     *
     * @param pest the pest to eat
     */
    EatPestAction(Actor pest) {
        this.pest = pest;
    }

    /**
     * Removes the pest from the map.
     *
     * @param actor the actor eating the pest
     * @param map the map containing the pest
     * @return what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        map.removeActor(pest);
        return actor + " eats " + pest + ".";
    }

    /**
     * @param actor the actor eating the pest
     * @return the menu text for this action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " eats " + pest;
    }
}
