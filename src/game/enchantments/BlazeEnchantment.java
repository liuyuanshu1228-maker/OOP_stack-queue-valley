package game.enchantments;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Fire;
import game.positions.area.TileListDescription;

import java.util.List;

/**
 * An Enchantment that ignites a {@link Fire} on every tile its host tool's
 * own effect touched, reusing the existing Fire item rather than a new
 * burning mechanism.
 */
public class BlazeEnchantment implements Enchantment {

    /** How many turns each ignited Fire burns for before going out. */
    private static final int FIRE_LIFETIME_TURNS = 5;

    /** Damage per turn a victim standing in one of these fires takes. */
    private static final int BURN_DAMAGE_PER_TURN = 5;

    /** How many turns the burn inflicted by these fires lasts on a victim. */
    private static final int BURN_DURATION_TURNS = 3;

    /**
     * Ignites a Fire on every given tile.
     *
     * @param affectedTiles the tiles the host tool's own effect touched
     * @param activator the Actor who triggered the effect
     * @return a description naming exactly which tiles caught fire
     */
    @Override
    public String applyTo(List<Location> affectedTiles, Actor activator) {
        if (affectedTiles.isEmpty()) {
            return "The Blaze Enchantment finds nothing to ignite.";
        }
        for (Location tile : affectedTiles) {
            tile.addItem(new Fire(FIRE_LIFETIME_TURNS, BURN_DAMAGE_PER_TURN, BURN_DURATION_TURNS));
        }
        return "Flames erupt at " + new TileListDescription().of(affectedTiles) + ".";
    }

    @Override
    public String toString() {
        return "Blaze Enchantment";
    }
}
