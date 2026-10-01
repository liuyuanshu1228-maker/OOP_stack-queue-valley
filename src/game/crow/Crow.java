package game.crow;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.CreatureStateAction;
import game.actors.CreatureState;
import game.actors.StatefulCreature;

/**
 * A crow: a stateful creature that cycles between perching, foraging,
 * mobbing nearby actors, and fleeing when threatened.
 * <p>
 * Only {@link Perching} is wired up so far; Foraging, Mobbing and Fleeing
 * are added incrementally.
 */
public class Crow extends Actor implements StatefulCreature {

    /** The crow's starting (and maximum) hit points. */
    private static final int STARTING_HP = 30;

    private CreatureState state = Perching.INSTANCE;

    /**
     * Constructor.
     *
     * @param name the crow's name
     */
    public Crow(String name) {
        super(name, 'v', STARTING_HP);
    }

    /**
     * Returns the state this crow is currently in.
     *
     * @return the current state
     */
    @Override
    public CreatureState getState() {
        return state;
    }

    /**
     * Switches this crow to a new state.
     *
     * @param state the state to switch to
     */
    @Override
    public void setState(CreatureState state) {
        this.state = state;
    }

    /**
     * Selects the action to perform on this turn: falls unconscious if out
     * of health, otherwise advances its state machine by one step.
     *
     * @see Actor#playTurn(ActionList, Action, GameMap, Display)
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction the Action this Actor took last turn
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return the Action to be performed
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        return new CreatureStateAction();
    }
}
