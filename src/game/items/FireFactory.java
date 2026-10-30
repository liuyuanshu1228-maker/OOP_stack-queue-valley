package game.items;

import edu.monash.fit2099.engine.items.Item;
import game.positions.area.ItemFactory;

/**
 * An ItemFactory that creates a new Fire with the same lifetime and burn
 * parameters each time it is asked.
 */
public class FireFactory implements ItemFactory {

    /** How many turns each created Fire burns for before going out. */
    private final int lifetimeTurns;
    /** Damage per turn of the burn each created Fire inflicts. */
    private final int burnDamagePerTurn;
    /** How many turns the burn each created Fire inflicts lasts. */
    private final int burnDurationTurns;

    /**
     * Creates a new FireFactory.
     *
     * @param lifetimeTurns how many turns each created Fire burns for
     * @param burnDamagePerTurn the damage per turn of the burn each created Fire inflicts
     * @param burnDurationTurns how many turns the burn each created Fire inflicts lasts
     */
    public FireFactory(int lifetimeTurns, int burnDamagePerTurn, int burnDurationTurns) {
        this.lifetimeTurns = lifetimeTurns;
        this.burnDamagePerTurn = burnDamagePerTurn;
        this.burnDurationTurns = burnDurationTurns;
    }

    /**
     * @return a newly constructed Fire with this factory's parameters
     */
    @Override
    public Item create() {
        return new Fire(lifetimeTurns, burnDamagePerTurn, burnDurationTurns);
    }
}
