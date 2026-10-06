package game.crow;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.CreatureStateAction;
import game.actors.Composure;
import game.actors.CreatureState;
import game.actors.ItemCache;
import game.actors.StatefulCreature;

import java.util.HashSet;
import java.util.Set;

/**
 * A crow: a stateful creature that cycles between {@link Perching},
 * {@link Foraging}, {@link Mobbing}, and {@link Fleeing}.
 */
public class Crow extends Actor implements StatefulCreature, ItemCache, Composure {

    /** The crow's starting (and maximum) hit points. */
    private static final int STARTING_HP = 30;

    /**
     * How far (in tiles) the crow can sense items and actors around it. Kept
     * small because the farm is only 10x7: a wider radius covers nearly the
     * whole farm, so the wandering animals and Merchant would always count as
     * a crowd and the crow would never leave Fleeing.
     */
    public static final int SENSING_RADIUS = 2;

    /** The state this crow is currently in. */
    private CreatureState state = Perching.INSTANCE;

    /** Items this crow has deliberately hidden away, so it does not forage them again. */
    private final Set<Item> cachedItems = new HashSet<>();

    /** Number of consecutive turns this crow has felt safe (no actors within sensing range). */
    private int safeTurns = 0;

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
     * @param item the Item to check
     * @return true if this crow remembers hiding this Item
     */
    @Override
    public boolean remembers(Item item) {
        return cachedItems.contains(item);
    }

    /**
     * Records that this crow has hidden the given Item.
     *
     * @param item the Item that was hidden
     */
    @Override
    public void remember(Item item) {
        cachedItems.add(item);
    }

    /**
     * Returns how many consecutive turns this crow has felt safe.
     *
     * @return the number of consecutive safe turns
     */
    @Override
    public int getSafeTurns() {
        return safeTurns;
    }

    /**
     * Records one more consecutive turn in which this crow felt safe.
     */
    @Override
    public void recordSafeTurn() {
        safeTurns++;
    }

    /**
     * Resets the count of consecutive safe turns back to zero.
     */
    @Override
    public void resetComposure() {
        safeTurns = 0;
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
