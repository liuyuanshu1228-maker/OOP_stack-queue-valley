package game.runes;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Fire;

import java.util.List;

/**
 * A Rune whose effect is fire: every affected tile catches alight, burning
 * whoever stands there for as long as the fire lasts.
 * <p>
 * Reuses the existing {@link Fire} item rather than inventing a second
 * burning mechanism (DRY) -- a rune-formation fire behaves exactly like any
 * other fire on the map.
 */
public class EmberRune implements Rune {

    /** How many turns the fire lingers on each affected tile before going out. */
    private static final int FIRE_LIFETIME_TURNS = 3;
    /** Damage per turn the fire deals to whoever is standing on it. */
    private static final int BURN_DAMAGE_PER_TURN = 10;
    /** How many turns the burn lasts on a victim once caught. */
    private static final int BURN_DURATION_TURNS = 3;

    @Override
    public String applyTo(List<Location> affectedTiles, Actor activator) {
        for (Location tile : affectedTiles) {
            tile.addItem(new Fire(FIRE_LIFETIME_TURNS, BURN_DAMAGE_PER_TURN, BURN_DURATION_TURNS));
        }
        return activator + " channels an Ember Rune, setting " + affectedTiles.size() + " tile(s) alight.";
    }
}
