package game.items.tools;

import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Farmer;
import game.positions.area.AreaFactory;
import game.positions.farm.Dirt;
import game.positions.mine.CopperNode;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests Pickaxe's enchanted chain-break effect: while enchanted, breaking
 * something has a chance of chaining to one adjacent breakable tile, then
 * fires the attached Enchantment against whatever the chain touched.
 */
class PickaxeTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);

    private final GameMap map = TestFarm.map("...", "...", "...");
    private final Farmer farmer = new Farmer("Test");


    @BeforeEach
    void setUp() {
    }


    @Test
    void unenchantedPickaxeTriggersNothing() {
        Pickaxe pickaxe = new Pickaxe(List.of(), random, areaFactory);

        String result = pickaxe.triggerEnchantment(farmer, map.at(1, 1));

        assertEquals("", result);
    }

    @Test
    void whenTheChainRollSucceedsBreaksTheAdjacentCopperNodeAndFiresTheEnchantment() {
        Pickaxe pickaxe = new Pickaxe(List.of(), random, areaFactory);
        pickaxe.enchant(new RecordingEnchantment());
        map.at(2, 1).setGround(new CopperNode(areaFactory));
        random.setRollsSucceed(true);

        String message = pickaxe.triggerEnchantment(farmer, map.at(1, 1));

        assertInstanceOf(Dirt.class, map.at(2, 1).getGround(), "the copper node should have broken");
        assertTrue(message.contains("(2, 1)"), message);
    }

    @Test
    void whenTheChainRollFailsNothingBreaksButTheEnchantmentStillFires() {
        Pickaxe pickaxe = new Pickaxe(List.of(), random, areaFactory);
        RecordingEnchantment enchantment = new RecordingEnchantment();
        pickaxe.enchant(enchantment);
        map.at(2, 1).setGround(new CopperNode(areaFactory));
        random.setRollsSucceed(false);

        pickaxe.triggerEnchantment(farmer, map.at(1, 1));

        assertInstanceOf(CopperNode.class, map.at(2, 1).getGround(), "nothing should have broken");
        assertEquals(1, enchantment.timesFired());
    }

    @Test
    void firingEmptiesTheEnchantmentSlot() {
        Pickaxe pickaxe = new Pickaxe(List.of(), random, areaFactory);
        pickaxe.enchant(new RecordingEnchantment());
        random.setRollsSucceed(false);

        pickaxe.triggerEnchantment(farmer, map.at(1, 1));
        String secondResult = pickaxe.triggerEnchantment(farmer, map.at(1, 1));

        assertEquals("", secondResult);
    }
}
