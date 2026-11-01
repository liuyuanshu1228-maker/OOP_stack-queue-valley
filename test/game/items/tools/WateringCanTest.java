package game.items.tools;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Farmer;
import game.crops.Strawberry;
import game.enchantments.BountyEnchantment;
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

    @Test
    void aBountyWateringCanMakesTheWateredStrawberryProduceAgain() {
        Location strawberryTile = map.at(2, 1);
        strawberryTile.setGround(new Strawberry(random, areaFactory));
        TestFarm.tick(strawberryTile, 5);
        WateringCan wateringCan = new WateringCan(List.of(), areaFactory);
        wateringCan.enchant(new BountyEnchantment());

        String message = wateringCan.triggerEnchantment(farmer, map.at(1, 1));

        assertTrue(message.contains("makes the plants grow further"), message);
        assertTrue(message.contains("Strawberry at " + strawberryTile + " drops a"), message);
    }
}
