package game.pestcontrol;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.WanderingActor;
import game.behaviours.WanderBehaviour;
import game.pests.PestPresent;
import game.random.RandomUtils;

/**
 * A ladybug released from a jar. Each turn it eats a pest next to it, or
 * wanders if there is none. It weakens by one hit point each turn, so it
 * lives only a few turns.
 */
public class Ladybug extends WanderingActor {

    /** Hit points lost each turn. */
    private final int ageingDamage = 1;

    /** Finds the pests next to the ladybug. */
    private final PestPresent pestFinder = new PestPresent();

    /**
     * Creates a ladybug, shown as 'e'. It starts with 5 hit points and
     * loses one each turn, so it lives for 5 turns.
     *
     * @param randomUtils the randomness used to build this ladybug's wandering behaviour
     */
    public Ladybug(RandomUtils randomUtils) {
        super("Ladybug", 'e', 5, new WanderBehaviour(randomUtils));
    }

    /**
     * Weakens by one hit point, then eats a pest next to it, or wanders.
     *
     * @param actions collection of possible Actions for this ladybug
     * @param lastAction the Action this ladybug took last turn
     * @param map the map containing the ladybug
     * @param display the I/O object to which messages may be written
     * @return the Action to be performed
     */
    @Override
    protected Action playConsciousTurn(ActionList actions, Action lastAction,
                                       GameMap map, Display display) {
        hurt(ageingDamage);
        for (Exit exit : map.locationOf(this).getExits()) {
            Location tile = exit.getDestination();
            if (pestFinder.isMetBy(tile)) {
                return new EatPestAction(tile.getActor());
            }
        }
        return super.playConsciousTurn(actions, lastAction, map, display);
    }
}
