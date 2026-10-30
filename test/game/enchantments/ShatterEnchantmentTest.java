package game.enchantments;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.pests.SlimeSnail;
import game.positions.area.AreaFactory;
import game.status.StunnedStatus;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the Shatter Enchantment stuns the Actor standing on every
 * affected tile, and that every message it returns -- the success message
 * and the "no one to stun" fallback alike -- names coordinates.
 */
class ShatterEnchantmentTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);

    private final GameMap map = TestFarm.map("...", "...", "...");

    @BeforeEach
    void registerMap() {
        new StackQueueValleyWorld(new Display()).addGameMap(map);
    }

    @Test
    void stunsTheActorStandingOnAnAffectedTile() throws Exception {
        SlimeSnail victim = new SlimeSnail(random, areaFactory);
        Location tile = map.at(1, 1);
        tile.addActor(victim);

        new ShatterEnchantment().applyTo(List.of(tile), null, map.at(0, 0));

        assertTrue(victim.hasStatus(StunnedStatus.class));
    }

    @Test
    void successMessageNamesTheStunnedTiles() throws Exception {
        SlimeSnail victim = new SlimeSnail(random, areaFactory);
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
