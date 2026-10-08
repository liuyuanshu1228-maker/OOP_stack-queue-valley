package game.crow;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.CreatureStateAction;
import game.actors.ConsciousActor;
import game.creaturestate.Composure;
import game.creaturestate.CreatureState;
import game.creaturestate.ItemCache;
import game.creaturestate.Roosting;
import game.creaturestate.StatefulCreature;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A crow: a stateful creature that cycles between {@link Perching},
 * {@link Foraging}, {@link Mobbing}, and {@link Fleeing}.
 */
public class Crow extends ConsciousActor implements StatefulCreature, ItemCache, Composure, Roosting {

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

    /** The perch this crow last settled on, or null if it has not settled yet. */
    private Location perch;

    /**
     * Creates a new Crow.
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
     * Returns the perch this crow last settled on.
     *
     * @return the perch, or empty if the crow has not settled yet
     */
    @Override
    public Optional<Location> getPerch() {
        return Optional.ofNullable(perch);
    }

    /**
     * Remembers the perch this crow has just settled on.
     *
     * @param perch the Location the crow settled on
     */
    @Override
    public void setPerch(Location perch) {
        this.perch = perch;
    }

    /**
     * Selects the action to perform on a conscious turn: reports what the crow
     * is carrying, then advances its state machine by one step.
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
        printInventory(display);
        return new CreatureStateAction();
    }

    /**
     * Prints what this crow is carrying, so stolen items can be followed from
     * turn to turn.
     *
     * @param display the I/O object to which the inventory is printed
     */
    private void printInventory(Display display) {
        List<Item> items = this.getInventory().getItems();
        if (items.isEmpty()) {
            display.println(this + " is carrying nothing.");
        } else {
            display.println(this + " is carrying: " + items);
        }
    }
}