package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.random.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The behaviour of an actor that wanders: each turn it steps onto a randomly
 * chosen adjacent tile that it is able to enter.
 * <p>
 * This is an engine {@link Behaviour}: a non-player actor uses it to decide
 * what to do on its own turn. It is shared by every actor that wanders (the
 * animals, the Merchant and the crow's perching state), so the rule for
 * choosing a step is written once. An actor that should move differently is
 * given a different {@link Behaviour} instead of overriding this one.
 */
public class WanderBehaviour implements Behaviour<Actor, Action> {

    /**
     * Picks one of the exits of the actor's location, uniformly at random,
     * among the exits whose destination the actor is able to enter.
     *
     * @param actor the actor that wants to wander
     * @param location the location the actor is standing on
     * @return the action that moves the actor through the chosen exit, or
     * null if the actor cannot enter any adjacent tile
     */
    @Override
    public Action operate(Actor actor, Location location) {
        List<Exit> enterableExits = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            if (exit.getDestination().canActorEnter(actor)) {
                enterableExits.add(exit);
            }
        }

        if (enterableExits.isEmpty()) {
            return null;
        }

        Exit chosen = RandomUtils.pickRandom(enterableExits);
        return new MoveActorAction(chosen.getDestination(), chosen.getName(), chosen.getHotKey());
    }
}
