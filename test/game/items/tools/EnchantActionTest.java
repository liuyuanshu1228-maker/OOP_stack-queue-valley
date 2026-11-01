package game.items.tools;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.actors.Farmer;
import game.positions.area.AreaFactory;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link EnchantAction}: attaching one specific Enchantment to one
 * specific Enchantable tool, and the two messages it produces. Uses
 * {@link Hoe} as a concrete stand-in for {@link Enchantable}; EnchantAction's
 * own behaviour does not depend on which kind of tool it is given.
 */
class EnchantActionTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);

    private final GameMap map = TestFarm.map("...", "...", "...");
    private final Farmer farmer = new Farmer("Test");

    @BeforeEach
    void registerMap() {
        new StackQueueValleyWorld(new Display()).addGameMap(map);
    }

    @Test
    void attachesTheEnchantmentToTheTool() throws Exception {
        Hoe hoe = new Hoe(List.of(), areaFactory);
        RecordingEnchantment enchantment = new RecordingEnchantment();
        Location here = map.at(1, 1);
        here.addActor(farmer);

        new EnchantAction(hoe, enchantment).execute(farmer, map);
        hoe.triggerEnchantment(farmer, here);

        assertEquals(1, enchantment.timesFired(), "the enchantment just attached should be the one that fires");
    }

    @Test
    void executeMessageNamesTheActorToolEnchantmentAndLocation() throws Exception {
        Hoe hoe = new Hoe(List.of(), areaFactory);
        RecordingEnchantment enchantment = new RecordingEnchantment();
        Location here = map.at(1, 1);
        here.addActor(farmer);

        String message = new EnchantAction(hoe, enchantment).execute(farmer, map);

        assertTrue(message.contains("(1, 1)"), message);
        assertTrue(message.contains(farmer.toString()), message);
        assertTrue(message.contains(hoe.toString()), message);
        assertTrue(message.contains(enchantment.toString()), message);
    }

    @Test
    void menuDescriptionNamesTheActorToolAndEnchantment() {
        Hoe hoe = new Hoe(List.of(), areaFactory);
        RecordingEnchantment enchantment = new RecordingEnchantment();

        String description = new EnchantAction(hoe, enchantment).menuDescription(farmer);

        assertTrue(description.contains(farmer.toString()), description);
        assertTrue(description.contains(hoe.toString()), description);
        assertTrue(description.contains(enchantment.toString()), description);
    }
}
