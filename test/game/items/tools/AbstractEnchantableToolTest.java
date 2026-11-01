package game.items.tools;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Farmer;
import game.positions.area.AreaFactory;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link AbstractEnchantableTool}'s own shared behaviour: offering one
 * {@link EnchantAction} per Enchantment the tool was given, each correctly
 * wired to attach that specific Enchantment, and exposing its
 * {@link EnchantmentSlot} to subclasses. Uses {@link Hoe} as a concrete
 * stand-in, since this behaviour is written once in the abstract class and
 * every subclass (Hoe, WateringCan, Pickaxe) inherits it unchanged.
 * <p>
 * Counts the offered Enchantments by comparing list sizes against a tool
 * offered none, and identifies the EnchantAction just added by position
 * (appended after the Item base class's own actions, in offer order) rather
 * than by type, so this test needs no {@code instanceof}.
 */
class AbstractEnchantableToolTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);

    private final GameMap map = TestFarm.map("...", "...", "...");
    private final Farmer farmer = new Farmer("Test");
    private final Location here = map.at(1, 1);

    @Test
    void offersOneEnchantActionPerOfferedEnchantment() {
        Hoe unenchantable = new Hoe(List.of(), areaFactory);
        int baseActionCount = unenchantable.allowableActions(farmer, map).size();

        RecordingEnchantment first = new RecordingEnchantment();
        RecordingEnchantment second = new RecordingEnchantment();
        Hoe hoe = new Hoe(List.of(first, second), areaFactory);

        ActionList actions = hoe.allowableActions(farmer, map);

        assertEquals(baseActionCount + 2, actions.size());
    }

    @Test
    void eachOfferedEnchantActionAttachesItsOwnEnchantment() {
        RecordingEnchantment first = new RecordingEnchantment();
        RecordingEnchantment second = new RecordingEnchantment();
        Hoe hoe = new Hoe(List.of(first, second), areaFactory);

        ActionList actions = hoe.allowableActions(farmer, map);
        Action lastOffered = actions.get(actions.size() - 1);
        lastOffered.execute(farmer, map);
        hoe.triggerEnchantment(farmer, here);

        assertEquals(0, first.timesFired(), "the first offered Enchantment was never chosen");
        assertEquals(1, second.timesFired(), "the last offered Enchantment is the one the last action attaches");
    }
}
