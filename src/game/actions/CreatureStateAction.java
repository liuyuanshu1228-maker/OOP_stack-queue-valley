package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.CreatureState;
import game.actors.StatefulCreature;

/**
 * An Action that advances a {@link StatefulCreature}'s state machine by one
 * turn: if the current state's conditions call for a transition, switches
 * to the next state and applies its entry effect; otherwise performs the
 * current state's ordinary behaviour.
 * <p>
 * This is deliberately generic: it knows nothing about any specific
 * creature or state, only the {@link StatefulCreature}/{@link CreatureState}
 * contracts, so any creature built on this framework reuses it unchanged.
 */
public class CreatureStateAction extends Action {

    /**
     * Advances the creature's state machine by one turn.
     *
     * @param actor the creature acting
     * @param map the map the creature is on
     * @return a description of what happened, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        StatefulCreature creature = actor.asCapability(StatefulCreature.class).orElseThrow();
        CreatureState current = creature.getState();
        CreatureState next = current.nextState(actor, map);

        if (next != current) {
            creature.setState(next);
            return next.onEnter(actor, map);
        }

        return current.act(actor, map);
    }

    /**
     * Describes what this Action will do if chosen in the menu. In
     * practice, a creature's own turn is never driven through the menu, so
     * this is never shown to the player.
     *
     * @param actor the creature performing the action
     * @return a generic description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " acts.";
    }
}
