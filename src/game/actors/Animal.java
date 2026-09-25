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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class for farm animals that wander randomly around the map and can be fed.
 * Subclasses need only define their heal amount and the item(s) produced when fed.
 */
public abstract class Animal extends Actor implements Feedable {

    private final Random random = new Random();

    /**
     * Constructor.
     *
     * @param name        the name of the Animal
     * @param displayChar the character used to represent the Animal
     * @param hitPoints   the Animal's starting (and maximum) hit points
     */
    public Animal(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);
    }

    /**
     * Selects this Animal's action for the turn: wanders randomly by choosing
     * a reachable exit from the current Location, or does nothing if no exit
     * can be entered.
     *
     * @param actions    collection of possible Actions for this Animal
     * @param lastAction the Action this Animal took last turn
     * @param map        the map containing the Animal
     * @param display    the I/O object for the game
     * @return a randomly selected MoveActorAction, or DoNothingAction if none are available
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        Location here = map.locationOf(this);
        List<Exit> enterableExits = new ArrayList<>();
        for (Exit exit : here.getExits()) {
            if (exit.getDestination().canActorEnter(this)) {
                enterableExits.add(exit);
            }
        }

        if (enterableExits.isEmpty()) {
            return new DoNothingAction();
        }

        Exit chosenExit = enterableExits.get(random.nextInt(enterableExits.size()));
        return new MoveActorAction(chosenExit.getDestination(), chosenExit.getName(), chosenExit.getHotKey());
    }
}
