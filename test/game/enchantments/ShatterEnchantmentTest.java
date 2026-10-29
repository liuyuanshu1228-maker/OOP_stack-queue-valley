package game.enchantments;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.pests.SlimeSnail;
import game.status.StunnedStatus;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the Shatter Enchantment stuns the Actor standing on every
 * affected tile, and that every message it returns -- the success message
 * and the "no one to stun" fallback alike -- names coordinates.
 */
class ShatterEnchantmentTest {

    private final GameMap map = TestFarm.map("...", "...", "...");

    @Test
    void stunsTheActorStandingOnAnAffectedTile() {
        SlimeSnail victim = new SlimeSnail();
        Location tile = map.at(1, 1);
        tile.addActor(victim);

        new ShatterEnchantment().applyTo(List.of(tile), null, map.at(0, 0));

        assertTrue(victim.hasStatus(StunnedStatus.class));
    }

    @Test
    void successMessageNamesTheStunnedTiles() {
        SlimeSnail victim = new SlimeSnail();
        Location tile = map.at(2, 1);
        tile.addActor(victim);

        String message = new ShatterEnchantment().applyTo(List.of(tile), null, map.at(0, 0));

        assertTrue(message.contains("(2, 1)"), message);
    }

    @Test
    void anAffectedTileWithNoActorIsSkippedAndNamesTheOriginInstead() {
        Location emptyTile = map.at(1, 1);
        Location origin = map.at(0, 0);

        String message = new ShatterEnchantment().applyTo(List.of(emptyTile), null, origin);

        assertTrue(message.contains("(0, 0)"), message);
    }

    @Test
    void emptyAffectedTilesStunsNoOneAndNamesTheOrigin() {
        Location origin = map.at(1, 1);

        String message = new ShatterEnchantment().applyTo(List.of(), null, origin);

        assertTrue(message.contains("(1, 1)"), message);
    }
}
