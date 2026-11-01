package game.actions;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.actors.Farmer;
import game.enchantments.BlazeEnchantment;
import game.items.Fire;
import game.items.tools.Hoe;
import game.items.tools.Pickaxe;
import game.items.tools.WateringCan;
import game.positions.area.AreaFactory;
import game.positions.farm.Dirt;
import game.positions.farm.Puddle;
import game.positions.farm.Soil;
import game.positions.mine.CopperNode;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end tests of REQ4's core flow: using an enchanted tool once, through
 * the Action a player actually chooses (TillAction, WaterAction, BreakAction),
 * triggers both effects together -- the tool's own amplified effect and the
 * attached Enchantment's effect -- not just {@code triggerEnchantment()}
 * called directly, which {@code HoeTest}/{@code WateringCanTest}/
 * {@code PickaxeTest} already cover in isolation.
 * <p>
 * Every scenario uses {@link BlazeEnchantment} as the attached Enchantment,
 * since its "Flames erupt" message is unambiguous and easy to tell apart
 * from the tool's own effect message.
 */
class EnchantedToolActionTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);
    private final Farmer farmer = new Farmer("Test");

    /**
     * Registers the given map with a world, the way every map used in these
     * tests needs to be, so {@code CapabilityToolAction}'s own Location
     * naming works.
     *
     * @param map the map to register
     */
    private void register(GameMap map) {
        new StackQueueValleyWorld(new Display()).addGameMap(map);
    }

    @Test
    void tillingWithAnEnchantedHoeFiresBothTheSpreadAndTheEnchantment() {
        // (1, 1) is the primary tilled tile, (2, 1) its one tillable
        // neighbour, surrounded by Soil so the spread is deterministic.
        GameMap map = TestFarm.map(",,,", ",..", ",,,");
        register(map);
        Hoe hoe = new Hoe(List.of(), areaFactory);
        hoe.enchant(new BlazeEnchantment());
        farmer.getInventory().add(hoe);
        Location here = map.at(1, 1);
        here.addActor(farmer);

        String message = new TillAction(here).execute(farmer, map);

        assertTrue(message.contains("tills the ground"), message);
        assertTrue(message.contains("enchantment spreads the tilling"), message);
        assertTrue(message.contains("Flames erupt"), message);
        assertEquals(Soil.class, map.at(2, 1).getGround().getClass(), "the spread-to tile should have been tilled");
        assertTrue(containsAFire(map.at(2, 1)), "the Blaze Enchantment should have fired on the spread-to tile");
    }

    @Test
    void tillingWithAnUnenchantedHoeFiresOnlyTheTillingEffect() {
        GameMap map = TestFarm.map(",,,", ",..", ",,,");
        register(map);
        Hoe hoe = new Hoe(List.of(), areaFactory);
        farmer.getInventory().add(hoe);
        Location here = map.at(1, 1);
        here.addActor(farmer);

        String message = new TillAction(here).execute(farmer, map);

        assertTrue(message.contains("tills the ground"), message);
        assertFalse(message.contains("enchantment"), message);
    }

    @Test
    void wateringWithAnEnchantedWateringCanFiresBothTheSpreadAndTheEnchantment() {
        // (1, 1) is the primary watered tile, (2, 1) its one waterable
        // neighbour, surrounded by Soil so the spread is deterministic.
        GameMap map = TestFarm.map(",,,", ",..", ",,,");
        register(map);
        WateringCan wateringCan = new WateringCan(List.of(), areaFactory);
        wateringCan.enchant(new BlazeEnchantment());
        farmer.getInventory().add(wateringCan);
        Location here = map.at(1, 1);
        here.addActor(farmer);

        String message = new WaterAction(here).execute(farmer, map);

        assertTrue(message.contains("waters the ground"), message);
        assertTrue(message.contains("enchantment also waters"), message);
        assertTrue(message.contains("Flames erupt"), message);
        assertEquals(Puddle.class, map.at(1, 1).getGround().getClass());
        assertEquals(Puddle.class, map.at(2, 1).getGround().getClass(), "the spread-to tile should have been watered");
        assertTrue(containsAFire(map.at(2, 1)), "the Blaze Enchantment should have fired on the spread-to tile");
    }

    @Test
    void breakingWithAnEnchantedPickaxeFiresBothTheChainAndTheEnchantment() {
        GameMap map = TestFarm.map("...", "...", "...");
        register(map);
        CopperNode primaryTarget = new CopperNode(areaFactory);
        map.at(1, 1).setGround(primaryTarget);
        map.at(2, 1).setGround(new CopperNode(areaFactory));
        Pickaxe pickaxe = new Pickaxe(List.of(), random, areaFactory);
        pickaxe.enchant(new BlazeEnchantment());
        farmer.getInventory().add(pickaxe);
        random.setRollsSucceed(true);

        String message = new BreakAction(primaryTarget, map.at(1, 1), "here").execute(farmer, map);

        assertTrue(message.contains("breaks the Copper node"), message);
        assertTrue(message.contains("enchantment's energy arcs to"), message);
        assertTrue(message.contains("Flames erupt"), message);
        assertEquals(Dirt.class, map.at(1, 1).getGround().getClass());
        assertEquals(Dirt.class, map.at(2, 1).getGround().getClass(), "the chained-to node should have broken too");
        assertTrue(containsAFire(map.at(2, 1)), "the Blaze Enchantment should have fired on the chained-to tile");
    }

    @Test
    void breakingWithAnUnenchantedPickaxeFiresOnlyTheBreakEffect() {
        GameMap map = TestFarm.map("...", "...", "...");
        register(map);
        CopperNode primaryTarget = new CopperNode(areaFactory);
        map.at(1, 1).setGround(primaryTarget);
        Pickaxe pickaxe = new Pickaxe(List.of(), random, areaFactory);
        farmer.getInventory().add(pickaxe);

        String message = new BreakAction(primaryTarget, map.at(1, 1), "here").execute(farmer, map);

        assertTrue(message.contains("breaks the Copper node"), message);
        assertFalse(message.contains("enchantment"), message);
    }

    /**
     * @param tile a tile
     * @return true if a Fire item is lying on the tile
     */
    private boolean containsAFire(Location tile) {
        for (Item item : tile.getItems()) {
            if (item.getClass() == Fire.class) {
                return true;
            }
        }
        return false;
    }
}
