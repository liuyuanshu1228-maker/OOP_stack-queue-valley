package game;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A puddle that holds an electric charge, created when a {@link Puddle} is
 * charged. It shocks whoever is standing on it, every turn they remain there.
 */
public class ChargedPuddle extends Ground {

    /** Damage dealt each turn to the Actor standing on this ground. */
    private static final int SHOCK_DAMAGE = 20;

    /**
     * Constructor.
     */
    public ChargedPuddle() {
        super('%', "Charged puddle");
    }

    /**
     * Shocks the Actor standing on this ground, if there is one.
     *
     * @param location the location of this ground
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            location.getActor().hurt(SHOCK_DAMAGE);
        }
    }
}