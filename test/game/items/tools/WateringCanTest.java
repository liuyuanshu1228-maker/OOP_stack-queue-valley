package game.items.tools;

import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Farmer;
import game.positions.area.AreaFactory;
import game.positions.farm.Puddle;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests WateringCan's enchanted spread effect: while enchanted, watering a
 * tile also waters one adjacent waterable tile, then fires the attached
 * Enchantment against whatever the spread touched.
 */
class WateringCanTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);

    /**
     * One Dirt tile ('.') surrounded by Soil (','), which is not Waterable --
     * so exactly one neighbour of the centre can be spread to, making the
     * pick deterministic without needing to control randomness.
     */
    private final GameMap map = TestFarm.map(",,,", ",,.", ",,,");

    private final Farmer farmer = new Farmer("Test");

    @Test
    void unenchantedWateringCanTriggersNothing() {
        WateringCan wateringCan = new WateringCan(List.of(), areaFactory);

        String result = wateringCan.triggerEnchantment(farmer, map.at(1, 1));

        assertEquals("", result);
    }

    @Test
    void watersTheAdjacentDirtAndFiresTheEnchantment() {
        WateringCan wateringCan = new WateringCan(List.of(), areaFactory);
        wateringCan.enchant(new RecordingEnchantment());

        String message = wateringCan.triggerEnchantment(farmer, map.at(1, 1));

        assertInstanceOf(Puddle.class, map.at(2, 1).getGround());
        assertTrue(message.contains("(2, 1)"), message);
    }

    @Test
    void whenNoAdjacentWaterableTileExistsReportsNothingToWaterButStillFiresTheEnchantment() {
        GameMap allSoil = TestFarm.map(",,,", ",,,", ",,,");
        WateringCan wateringCan = new WateringCan(List.of(), areaFactory);
        RecordingEnchantment enchantment = new RecordingEnchantment();
        wateringCan.enchant(enchantment);

        String message = wateringCan.triggerEnchantment(farmer, allSoil.at(1, 1));

        assertEquals(1, enchantment.timesFired());
        assertTrue(message.contains("no adjacent ground to water"), message);
    }

    @Test
    void firingEmptiesTheEnchantmentSlot() {
        WateringCan wateringCan = new WateringCan(List.of(), areaFactory);
        wateringCan.enchant(new RecordingEnchantment());

        wateringCan.triggerEnchantment(farmer, map.at(1, 1));
        String secondResult = wateringCan.triggerEnchantment(farmer, map.at(1, 1));

        assertEquals("", secondResult);
    }
}
