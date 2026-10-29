package game.enchantments;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.area.TileListDescription;
import game.status.StunnedStatus;
import game.status.TimedStatus;

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
     * @param origin the Location the host tool was used at
     * @return a description naming exactly which tiles had an Actor stunned
     */
    @Override
    public String applyTo(List<Location> affectedTiles, Actor activator, Location origin) {
        List<Location> stunnedTiles = new ArrayList<>();
        for (Location tile : affectedTiles) {
            if (tile.containsAnActor()) {
                new StunnedStatus(STUN_DURATION_TURNS).replaceOn(tile.getActor());
                stunnedTiles.add(tile);
            }
        }
        if (stunnedTiles.isEmpty()) {
            return "The Shatter Enchantment finds no one to stun near " + origin + ".";
        }
        return "Actors are stunned at " + new TileListDescription().of(stunnedTiles) + ".";
    }

    @Override
    public String toString() {
        return "Shatter Enchantment";
    }
}
