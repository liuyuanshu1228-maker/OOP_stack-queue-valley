package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.status.IncapacitatingStatus;

/**
 * Base class for actors that can only act while they are conscious.
 * <p>
 * Every turn it first checks whether the actor has run out of hit points; if so,
 * the actor falls unconscious and does nothing. It then checks whether the actor
 * is incapacitated by some other means (e.g. Stunned by a Quake Rune), in which
 * case it also does nothing. Otherwise the turn is handed to
 * {@link #playConsciousTurn}, which each subclass implements with its own way of
 * choosing an action (a menu for the player, a behaviour or a state machine for
 * other actors). This keeps both checks in a single place.
 */
public abstract class ConsciousActor extends Actor {

    /**
     * Creates a new ConsciousActor.
     *
     * @param name the name of the Actor
     * @param displayChar the character that will represent the Actor in the display
     * @param hitPoints the Actor's starting hit points
     */
    public ConsciousActor(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);
    }

    /**
     * Selects the action to perform on this turn: falls unconscious and does
     * nothing if out of hit points, does nothing if incapacitated by some
     * other status, otherwise lets the subclass choose.
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction the Action this Actor took last turn
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the Action to be performed
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }
        if (this.hasStatus(IncapacitatingStatus.class)) {
            return new DoNothingAction();
        }
        return playConsciousTurn(actions, lastAction, map, display);
    }

    /**
     * Chooses the action to perform on a turn where this actor is conscious
     * and not incapacitated.
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction the Action this Actor took last turn
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the Action to be performed
     */
    protected abstract Action playConsciousTurn(ActionList actions, Action lastAction,
                                                GameMap map, Display display);
}
