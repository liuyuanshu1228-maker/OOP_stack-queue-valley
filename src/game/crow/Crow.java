package game.crow;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.CreatureStateAction;
import game.actors.ConsciousActor;
import game.creaturestate.AreaFactorySource;
import game.creaturestate.Composure;
import game.creaturestate.CreatureState;
import game.creaturestate.ItemCache;
import game.creaturestate.RandomnessSource;
import game.creaturestate.StatefulCreature;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * A crow: a stateful creature that cycles between {@link Perching},
 * {@link Foraging}, {@link Mobbing}, and {@link Fleeing}.
 * <p>
 * It is displayed as 'b' (for bird), since 'v' is the Strawberry Sprout.
 * <p>
 * The crow's states must stay constructible with no arguments (a new
 * instance is created on every transition), so they cannot hold their own
 * {@link RandomUtils} or {@link AreaFactory}. Instead the crow carries both
 * and exposes them through the {@link game.creaturestate.RandomnessSource} and
 * {@link game.creaturestate.AreaFactorySource} capabilities, which its states query at the
 * point of use.
 */
public class Crow extends ConsciousActor
        implements StatefulCreature, ItemCache, Composure, SensingRange, AreaFactorySource, RandomnessSource {

    /** The crow's starting (and maximum) hit points. */
    private static final int STARTING_HP = 30;

    /**
     * How far (in tiles) the crow can sense items and actors around it. Kept
     * small because the farm is only 10x7: a wider radius covers nearly the
     * whole farm, so the wandering animals and Merchant would always count as
     * a crowd and the crow would never leave Fleeing.
     */
    private static final int SENSING_RADIUS = 2;

    /** The state this crow is currently in. */
    private CreatureState state = new Perching();

    /** Items this crow has deliberately hidden away, so it does not forage them again. */
    private final Set<Item> cachedItems = new HashSet<>();

    /** Number of consecutive turns this crow has felt safe (no actors within sensing range). */
    private int safeTurns = 0;

    /** The randomness this crow hands to its states when they need one. */
    private final RandomUtils randomUtils;

    /** The AreaFactory this crow hands to its states when they need one. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new Crow.
     *
     * @param name the crow's name
     * @param randomUtils the randomness this crow hands to its states when they need one
     * @param areaFactory the AreaFactory this crow hands to its states when they need one
     */
    public Crow(String name, RandomUtils randomUtils, AreaFactory areaFactory) {
        super(name, 'b', STARTING_HP);
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
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
     * @return how far, in tiles, this crow can sense around itself
     */
    @Override
    public int sensingRadius() {
        return SENSING_RADIUS;
    }

    /**
     * @return the randomness this crow hands to its states when they need one
     */
    @Override
    public RandomUtils randomUtils() {
        return randomUtils;
    }

    /**
     * @return the AreaFactory this crow hands to its states when they need one
     */
    @Override
    public AreaFactory areaFactory() {
        return areaFactory;
    }

    /**
     * Selects the action to perform on a conscious turn: advances its state
     * machine by one step.
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
        return new CreatureStateAction();
    }
}
