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

/**
 * A ladybug released from a jar. Each turn it eats a pest next to it, or
 * wanders if there is none. It weakens by one hit point each turn, so it
 * lives only a few turns.
 */
public class Ladybug extends WanderingActor {

    /** The name of this actor. */
    private static final String NAME = "Ladybug";

    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = 'e';

    /** Starting hit points, which is also how many turns it lives. */
    private static final int LIFESPAN_TURNS = 5;

    /** Hit points lost each turn. */
    private static final int AGEING_DAMAGE = 1;

    /** Finds the pests next to the ladybug. */
    private final PestPresent pestFinder = new PestPresent();

    /**
     * Creates a ladybug.
     */
    public Ladybug() {
        super(NAME, DISPLAY_CHAR, LIFESPAN_TURNS, new WanderBehaviour());
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
        hurt(AGEING_DAMAGE);
        for (Exit exit : map.locationOf(this).getExits()) {
            Location tile = exit.getDestination();
            if (pestFinder.isMetBy(tile)) {
                return new EatPestAction(tile.getActor());
            }
        }
        return super.playConsciousTurn(actions, lastAction, map, display);
    }
}
