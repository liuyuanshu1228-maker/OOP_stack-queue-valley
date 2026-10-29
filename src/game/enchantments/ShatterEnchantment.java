package game.enchantments;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.area.TileListDescription;
import game.status.StunnedStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * An Enchantment that stuns any Actor standing on a tile its host tool's own
 * effect touched, reusing the existing StunnedStatus rather than a new
 * status effect.
 */
public class ShatterEnchantment implements Enchantment {

    /** How many turns an Actor caught by this Enchantment is stunned for. */
    private static final int STUN_DURATION_TURNS = 2;

    /**
     * Stuns the Actor standing on each given tile, if any.
     *
     * @param affectedTiles the tiles the host tool's own effect touched
     * @param activator the Actor who triggered the effect
     * @return a description naming exactly which tiles had an Actor stunned
     */
    @Override
    public String applyTo(List<Location> affectedTiles, Actor activator) {
        List<Location> stunnedTiles = new ArrayList<>();
        for (Location tile : affectedTiles) {
            if (tile.containsAnActor()) {
                StunnedStatus.applyTo(tile.getActor(), STUN_DURATION_TURNS);
                stunnedTiles.add(tile);
            }
        }
        if (stunnedTiles.isEmpty()) {
            return "The Shatter Enchantment finds no one to stun.";
        }
        return "Actors are stunned at " + new TileListDescription().of(stunnedTiles) + ".";
    }

    @Override
    public String toString() {
        return "Shatter Enchantment";
    }
}
