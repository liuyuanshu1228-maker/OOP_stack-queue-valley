package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Fire lying on the ground. It cannot be picked up, burns for a limited number
 * of turns and, on every turn of its life, burns whoever is standing on its
 * tile. Because the burn is reapplied every turn, an Actor who stays in the
 * fire keeps burning for as long as the fire lasts.
 * <p>
 * How long the fire lasts, and how hard it burns, are supplied by whatever
 * lights it (a teleportation circle, an exploding geode, ...).
 */
public class Fire extends Item {

    /** Damage per turn applied to any Actor standing on this fire's tile. */
    private final int burnDamagePerTurn;
    /** Number of turns the BurningStatus inflicted by this fire lasts on the victim. */
    private final int burnDurationTurns;
    /** Turns of life remaining before this fire extinguishes itself. */
    private int remainingTurns;

    /**
     * Creates a new Fire.
     *
     * @param lifetimeTurns how many turns this fire burns before going out
     * @param burnDamagePerTurn the damage per turn of the burn it inflicts
     * @param burnDurationTurns how many turns the burn it inflicts lasts
     */
    public Fire(int lifetimeTurns, int burnDamagePerTurn, int burnDurationTurns) {
        super("Fire", '^');
        this.remainingTurns = lifetimeTurns;
        this.burnDamagePerTurn = burnDamagePerTurn;
        this.burnDurationTurns = burnDurationTurns;
    }

    /**
     * Burns the Actor standing on this fire (if any), then ages the fire and
     * removes it from the ground once it has burned out.
     *
     * @param currentLocation the location of the ground this fire lies on
     */
    @Override
    public void tick(Location currentLocation) {
        if (currentLocation.containsAnActor()) {
            BurningStatus.applyTo(currentLocation.getActor(), burnDamagePerTurn, burnDurationTurns);
        }
        remainingTurns--;
        if (remainingTurns <= 0) {
            currentLocation.removeItem(this);
        }
    }
}