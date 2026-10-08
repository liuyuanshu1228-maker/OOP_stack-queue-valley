package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;

/**
 * Abstract base class for non-player actors that move around the map by
 * themselves each turn.
 * <p>
 * How such an actor chooses its move is the job of an engine {@link Behaviour},
 * which is given to it when it is created: by default a {@link WanderBehaviour},
 * which steps onto a random tile that the actor can enter. The actor itself only
 * decides whether it can act at all, so a variant that moves differently needs a
 * different behaviour, not a different class hierarchy.
 */
public abstract class WanderingActor extends ConsciousActor {

    /** Decides where this actor goes on its turn. */
    private final Behaviour<Actor, Action> behaviour;

    /**
     * Creates a WanderingActor that wanders at random.
     *
     * @param name the name of the Actor
     * @param displayChar the character that will represent the Actor in the display
     * @param hitPoints the Actor's starting hit points
     */
    public WanderingActor(String name, char displayChar, int hitPoints) {
        this(name, displayChar, hitPoints, new WanderBehaviour());
    }

    /**
     * Creates a WanderingActor that moves according to the given behaviour.
     *
     * @param name the name of the Actor
     * @param displayChar the character that will represent the Actor in the display
     * @param hitPoints the Actor's starting hit points
     * @param behaviour decides where the Actor goes on its turn
     */
    public WanderingActor(String name, char displayChar, int hitPoints,
                          Behaviour<Actor, Action> behaviour) {
        super(name, displayChar, hitPoints);
        this.behaviour = behaviour;
    }

    /**
     * Selects the Action to perform on a conscious turn: does what its
     * behaviour decides, or nothing if the behaviour finds no valid move.
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction the Action this Actor took last turn
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the Action to be performed
     */
    @Override
    protected Action playConsciousTurn(ActionList actions, Action lastAction,
                                       GameMap map, Display display) {
        Action chosen = behaviour.operate(this, map.locationOf(this));
        if (chosen == null) {
            return new DoNothingAction();
        }
        return chosen;
    }
}
