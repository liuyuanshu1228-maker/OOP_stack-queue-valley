package game.enchantments;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Fire;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the Blaze Enchantment ignites a Fire on every affected tile,
 * and that every message it returns -- the success message and the "nothing
 * to ignite" fallback alike -- names the coordinates of the relevant
 * tile(s), per the assignment's rule that every triggered effect's message
 * must say where it happened.
 */
class BlazeEnchantmentTest {

    private final GameMap map = TestFarm.map("...", "...", "...");

    @Test
    void ignitesFireOnEveryAffectedTile() {
        Location tile = map.at(1, 1);

        new BlazeEnchantment().applyTo(List.of(tile), null, map.at(0, 0));

        assertTrue(containsAFire(tile));
    }

    @Test
    void successMessageNamesTheAffectedTiles() {
        Location tile = map.at(2, 1);

        String message = new BlazeEnchantment().applyTo(List.of(tile), null, map.at(0, 0));

        assertTrue(message.contains("(2, 1)"), message);
    }

    @Test
    void emptyAffectedTilesIgnitesNothingAndNamesTheOrigin() {
        Location origin = map.at(1, 1);

        String message = new BlazeEnchantment().applyTo(List.of(), null, origin);

        assertFalse(containsAFire(origin));
        assertTrue(message.contains("(1, 1)"), message);
    }

    /**
     * @param tile a tile
     * @return true if a Fire item is lying on the tile
     */
    private boolean containsAFire(Location tile) {
        for (Item item : tile.getItems()) {
            if (item.getClass() == Fire.class) {
                return true;
            }
        }
        return false;
    }
}
