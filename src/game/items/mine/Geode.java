package game.items.mine;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BreakAction;
import game.items.Fire;
import game.positions.Breakable;
import game.positions.LocationUtils;

import java.util.Random;

/**
 * A geode. It can be picked up and carried, and can be broken either from the
 * inventory or while it lies on the ground (from the tile it lies on). Either
 * way the breaker is standing on the tile where it is broken, so the outcome is
 * centred on that tile.
 * <p>
 * Breaking a geode removes it and either produces a diamond or makes it
 * explode.
 */
public class Geode extends Item implements Breakable {

    /** Chance (out of 100) that a broken geode splits open to reveal a diamond. */
    private static final int DIAMOND_CHANCE_PERCENT = 50;
    /** Damage the explosion deals to every Actor at its centre and next to it. */
    private static final int EXPLOSION_DAMAGE = 25;
    /** How many turns the fire lit by the explosion burns for. */
    private static final int FIRE_LIFETIME_TURNS = 5;
    /** Damage per turn of the burn the fire inflicts. */
    private static final int BURN_DAMAGE_PER_TURN = 10;
    /** How many turns the burn the fire inflicts lasts. */
    private static final int BURN_DURATION_TURNS = 4;

    /** Context label used when the BreakAction is offered from the carrier's inventory. */
    private static final String IN_INVENTORY = "in inventory";
    /** Context label used when the BreakAction is offered from the tile the Geode lies on. */
    private static final String ON_THE_GROUND = "on the ground";

    /** Random number generator used to decide the outcome of breaking this Geode. */
    private final Random random = new Random();

    /**
     * Constructor.
     */
    public Geode() {
        super("Geode", 'g');
        this.makePortable();
    }

    /**
     * Offers to break this geode while it is being carried.
     *
     * @param owner the Actor carrying this geode
     * @param map the map the owner is on
     * @return an ActionList containing a BreakAction
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        return new ActionList(new BreakAction(this, map.locationOf(owner), IN_INVENTORY));
    }

    /**
     * Offers to break this geode while it lies on the ground, to the Actor
     * standing on the same tile.
     *
     * @param location the location of the ground on which this geode lies
     * @return an ActionList containing a BreakAction
     */
    @Override
    public ActionList allowableActions(Location location) {
        return new ActionList(new BreakAction(this, location, ON_THE_GROUND));
    }

    /**
     * Removes this geode from wherever it was (the inventory or the ground),
     * then either produces a diamond or explodes, hurting every Actor on and
     * around the tile and setting the tiles around it on fire.
     *
     * @param actor the Actor breaking this geode
     * @param location the Location the geode is broken at
     * @return a description of the outcome
     */
    @Override
    public String breakUp(Actor actor, Location location) {
        if (actor.getInventory().getItems().contains(this)) {
            actor.getInventory().remove(this);
        } else {
            location.removeItem(this);
        }
        if (random.nextInt(100) < DIAMOND_CHANCE_PERCENT) {
            location.addItem(new Diamond());
            return actor + " breaks the " + this + ". It splits open, revealing a diamond!";
        }
        LocationUtils.hurtActorsAround(location, EXPLOSION_DAMAGE);
        LocationUtils.addItemToEachNearby(location,
                () -> new Fire(FIRE_LIFETIME_TURNS, BURN_DAMAGE_PER_TURN, BURN_DURATION_TURNS));
        return actor + " breaks the " + this + ". It explodes, dealing " + EXPLOSION_DAMAGE
                + " damage to everyone nearby and setting the ground alight!";
    }
}
