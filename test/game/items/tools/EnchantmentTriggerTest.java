package game.items.tools;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Farmer;
import game.positions.area.AreaFactory;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link EnchantmentTrigger} in isolation from any Action: given the
 * exact tool instance an Action already found, it must fire that instance's
 * own Enchantment and no other, and must do so by reference equality, not by
 * type -- the reason given in {@link EnchantmentTrigger}'s own javadoc for
 * why it needs no {@code instanceof}.
 * <p>
 * Uses {@link Hoe} as a concrete stand-in for {@link Enchantable}, since the
 * trigger's own behaviour does not depend on which tool it is given. Every
 * map used is all Soil, so no Hoe's own tilling-spread effect has anything
 * to touch, and only the Enchantment firing is in play.
 */
class EnchantmentTriggerTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);

    private final GameMap map = TestFarm.map(",,,", ",,,", ",,,");
    private final Location location = map.at(1, 1);
    private final Farmer farmer = new Farmer("Test");

    private final EnchantmentTrigger trigger = new EnchantmentTrigger();

    @Test
    void firesTheGivenToolsEnchantmentAndPrependsASpace() {
        Hoe hoe = new Hoe(List.of(), areaFactory);
        hoe.enchant(new RecordingEnchantment());
        farmer.getInventory().add(hoe);

        String result = trigger.fire(farmer, hoe, location);

        assertTrue(result.startsWith(" "), result);
        assertTrue(result.contains("fired"), result);
    }

    @Test
    void returnsEmptyWhenTheToolIsUnenchanted() {
        Hoe hoe = new Hoe(List.of(), areaFactory);
        farmer.getInventory().add(hoe);

        String result = trigger.fire(farmer, hoe, location);

        assertEquals("", result);
    }

    @Test
    void returnsEmptyWhenTheActorCarriesNoMatchingEnchantableTool() {
        Hoe hoeNotInAnyInventory = new Hoe(List.of(), areaFactory);
        hoeNotInAnyInventory.enchant(new RecordingEnchantment());

        String result = trigger.fire(farmer, hoeNotInAnyInventory, location);

        assertEquals("", result);
    }

    @Test
    void firesOnlyTheExactToolInstanceAmongSeveralOfTheSameType() {
        Hoe usedHoe = new Hoe(List.of(), areaFactory);
        RecordingEnchantment usedEnchantment = new RecordingEnchantment();
        usedHoe.enchant(usedEnchantment);
        farmer.getInventory().add(usedHoe);

        Hoe otherHoe = new Hoe(List.of(), areaFactory);
        RecordingEnchantment otherEnchantment = new RecordingEnchantment();
        otherHoe.enchant(otherEnchantment);
        farmer.getInventory().add(otherHoe);

        trigger.fire(farmer, usedHoe, location);

        assertEquals(1, usedEnchantment.timesFired());
        assertEquals(0, otherEnchantment.timesFired());
    }
}
