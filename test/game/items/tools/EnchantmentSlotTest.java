package game.items.tools;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.pests.SlimeSnail;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link EnchantmentSlot}'s store/query/fire/consume-once behaviour,
 * independent of any particular tool or Enchantment -- every enchantable
 * tool (Hoe, WateringCan, Pickaxe) delegates to this one class for that
 * behaviour, so testing it here covers all three at once.
 */
class EnchantmentSlotTest {

    private final GameMap map = TestFarm.map("...", "...", "...");

    /** Stands in for whichever Actor triggered the tool; never dereferenced by this test. */
    private final SlimeSnail activator = new SlimeSnail();

    @Test
    void startsEmpty() {
        EnchantmentSlot slot = new EnchantmentSlot();

        assertTrue(slot.isEmpty());
    }

    @Test
    void enchantingFillsTheSlot() {
        EnchantmentSlot slot = new EnchantmentSlot();

        slot.enchant(new RecordingEnchantment());

        assertFalse(slot.isEmpty());
    }

    @Test
    void firingPassesTheTilesActivatorAndOriginThrough() {
        EnchantmentSlot slot = new EnchantmentSlot();
        RecordingEnchantment enchantment = new RecordingEnchantment();
        slot.enchant(enchantment);
        List<Location> tiles = List.of(map.at(0, 0));
        Location origin = map.at(1, 1);

        slot.fire(tiles, activator, origin);

        assertEquals(1, enchantment.timesFired());
        assertSame(tiles, enchantment.lastAffectedTiles());
        assertSame(activator, enchantment.lastActivator());
        assertSame(origin, enchantment.lastOrigin());
    }

    @Test
    void firingReturnsTheEnchantmentsMessage() {
        EnchantmentSlot slot = new EnchantmentSlot();
        slot.enchant(new RecordingEnchantment());

        String result = slot.fire(List.of(), activator, map.at(1, 1));

        assertEquals("fired", result);
    }

    @Test
    void firingEmptiesTheSlot() {
        EnchantmentSlot slot = new EnchantmentSlot();
        slot.enchant(new RecordingEnchantment());

        slot.fire(List.of(), activator, map.at(1, 1));

        assertTrue(slot.isEmpty());
    }

    @Test
    void firingAnEmptySlotDoesNothingAndReturnsAnEmptyString() {
        EnchantmentSlot slot = new EnchantmentSlot();

        String result = slot.fire(List.of(), activator, map.at(1, 1));

        assertEquals("", result);
    }

    @Test
    void firingTwiceOnlyFiresTheEnchantmentOnce() {
        EnchantmentSlot slot = new EnchantmentSlot();
        RecordingEnchantment enchantment = new RecordingEnchantment();
        slot.enchant(enchantment);

        slot.fire(List.of(), activator, map.at(1, 1));
        slot.fire(List.of(), activator, map.at(1, 1));

        assertEquals(1, enchantment.timesFired());
    }

    @Test
    void enchantingReplacesWhateverWasAlreadyAttached() {
        EnchantmentSlot slot = new EnchantmentSlot();
        RecordingEnchantment oldEnchantment = new RecordingEnchantment();
        RecordingEnchantment newEnchantment = new RecordingEnchantment();
        slot.enchant(oldEnchantment);

        slot.enchant(newEnchantment);
        slot.fire(List.of(), activator, map.at(1, 1));

        assertEquals(0, oldEnchantment.timesFired());
        assertEquals(1, newEnchantment.timesFired());
    }
}
