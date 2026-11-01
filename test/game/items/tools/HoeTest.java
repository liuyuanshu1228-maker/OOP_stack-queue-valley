package game.items.tools;

import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Farmer;
import game.positions.area.AreaFactory;
import game.positions.farm.Soil;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests Hoe's enchanted tilling-spread effect: while enchanted, tilling a
 * tile also tills every tillable tile adjacent to it, then fires the
 * attached Enchantment against whatever the spread touched.
 */
class HoeTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);

    private final Farmer farmer = new Farmer("Test");

    @Test
    void unenchantedHoeTriggersNothing() {
        GameMap map = TestFarm.map("...", "...", "...");
        Hoe hoe = new Hoe(List.of(), areaFactory);

        String result = hoe.triggerEnchantment(farmer, map.at(1, 1));

        assertEquals("", result);
    }

    @Test
    void tillsTheOneAdjacentDirtAndFiresTheEnchantment() {
        // One Dirt tile ('.') surrounded by Soil (','), which is not
        // Tillable -- so exactly one neighbour of the centre can be tilled,
        // making the spread deterministic without needing to control
        // randomness.
        GameMap map = TestFarm.map(",,,", ",,.", ",,,");
        Hoe hoe = new Hoe(List.of(), areaFactory);
        hoe.enchant(new RecordingEnchantment());

        String message = hoe.triggerEnchantment(farmer, map.at(1, 1));

        assertInstanceOf(Soil.class, map.at(2, 1).getGround(), "the dirt should have been tilled");
        assertTrue(message.contains("(2, 1)"), message);
    }

    @Test
    void tillsEveryAdjacentTillableTile() {
        // Every one of the eight tiles adjacent to the centre is Dirt, so
        // the spread should till all eight of them, not just one -- unlike
        // WateringCan and Pickaxe, Hoe's own effect is not a single random
        // pick.
        GameMap map = TestFarm.map("...", ".,.", "...");
        Hoe hoe = new Hoe(List.of(), areaFactory);
        hoe.enchant(new RecordingEnchantment());

        hoe.triggerEnchantment(farmer, map.at(1, 1));

        assertInstanceOf(Soil.class, map.at(0, 0).getGround());
        assertInstanceOf(Soil.class, map.at(1, 0).getGround());
        assertInstanceOf(Soil.class, map.at(2, 0).getGround());
        assertInstanceOf(Soil.class, map.at(0, 1).getGround());
        assertInstanceOf(Soil.class, map.at(2, 1).getGround());
        assertInstanceOf(Soil.class, map.at(0, 2).getGround());
        assertInstanceOf(Soil.class, map.at(1, 2).getGround());
        assertInstanceOf(Soil.class, map.at(2, 2).getGround());
    }

    @Test
    void whenNoAdjacentTillableTileExistsReportsNothingToTillButStillFiresTheEnchantment() {
        GameMap allSoil = TestFarm.map(",,,", ",,,", ",,,");
        Hoe hoe = new Hoe(List.of(), areaFactory);
        RecordingEnchantment enchantment = new RecordingEnchantment();
        hoe.enchant(enchantment);

        String message = hoe.triggerEnchantment(farmer, allSoil.at(1, 1));

        assertEquals(1, enchantment.timesFired());
        assertTrue(message.contains("no adjacent ground to till"), message);
    }

    @Test
    void firingEmptiesTheEnchantmentSlot() {
        GameMap map = TestFarm.map(",,,", ",,.", ",,,");
        Hoe hoe = new Hoe(List.of(), areaFactory);
        hoe.enchant(new RecordingEnchantment());

        hoe.triggerEnchantment(farmer, map.at(1, 1));
        String secondResult = hoe.triggerEnchantment(farmer, map.at(1, 1));

        assertEquals("", secondResult);
    }
}
