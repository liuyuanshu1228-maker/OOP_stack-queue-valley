package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.Chance;

/**
 * Abstract base class for actors that wander randomly around the map each turn.
 *
 */
public abstract class WanderingActor extends Actor {

    /**
     * Constructor for WanderingActor.
     *
     * @param name        the name of the Actor
     * @param displayChar the character that will represent the Actor in the display
     * @param hitPoints   the Actor's starting hit points
     */
    public WanderingActor(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);
    }

    /**
     * Selects and returns an Action to perform on the current turn.
     * Falls unconscious if out of hit points; otherwise moves to a randomly
     * chosen enterable adjacent exit, or does nothing if no exit is available.
     *
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

        Location here = map.locationOf(this);
        var enterableExits = here.getExits().stream()
                .filter(exit -> exit.getDestination().canActorEnter(this))
                .toList();

        var chosenExit = Chance.pickOne(enterableExits);
        if (chosenExit.isEmpty()) {
            return new DoNothingAction();
        }

        Exit exit = chosenExit.get();
        return new MoveActorAction(exit.getDestination(), exit.getName(), exit.getHotKey());
    }
}