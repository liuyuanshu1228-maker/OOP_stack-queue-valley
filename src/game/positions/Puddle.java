package game.positions;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A puddle on the ground. It starts uncharged and can become a charged puddle
 * when a {@link CopperNode} is broken nearby. A charged puddle shocks whoever
 * stands on it every turn.
 */
public class Puddle extends Ground implements Chargeable {

    /** Damage dealt each turn to the Actor standing on a charged puddle. */
    private static final int SHOCK_DAMAGE = 20;

    private final boolean charged;

    /**
     * Constructs an uncharged puddle.
     */
    public Puddle() {
        this(false);
    }

    /**
     * Constructs a puddle in the given charge state.
     *
     * @param charged true for a charged puddle, false for a normal puddle
     */
    private Puddle(boolean charged) {
        super(charged ? '%' : '~', charged ? "Charged puddle" : "Puddle");
        this.charged = charged;
    }

    /**
     * Returns a charged version of this puddle.
     *
     * @return a new charged Puddle
     */
    @Override
    public Ground getChargedResult() {
        return new Puddle(true);
    }

    /**
     * Shocks the Actor standing on this puddle each turn, if it is charged.
     *
     * @param location the location of this puddle
     */
    @Override
    public void tick(Location location) {
        if (charged && location.containsAnActor()) {
            location.getActor().hurt(SHOCK_DAMAGE);
        }
    }
}