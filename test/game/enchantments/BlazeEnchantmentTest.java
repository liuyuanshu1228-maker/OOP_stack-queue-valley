package game.enchantments;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.actors.Farmer;
import game.items.Fire;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests that the Blaze Enchantment ignites a Fire on every affected tile,
 * and that every message it returns -- the success message and the "nothing
 * to ignite" fallback alike -- names the coordinates of the relevant
 * tile(s), per the assignment's rule that every triggered effect's message
 * must say where it happened.
 */
class BlazeEnchantmentTest {

    private final GameMap map = TestFarm.map("...", "...", "...");

    @Test
    void ignitesFireOnEveryAffectedTile() {
        Location tile = map.at(1, 1);

        new BlazeEnchantment().applyTo(List.of(tile), null, map.at(0, 0));

        assertTrue(containsAFire(tile));
    }

    @Test
    void successMessageNamesTheAffectedTiles() {
        Location tile = map.at(2, 1);

        String message = new BlazeEnchantment().applyTo(List.of(tile), null, map.at(0, 0));

        assertTrue(message.contains("(2, 1)"), message);
    }

    @Test
    void emptyAffectedTilesIgnitesNothingAndNamesTheOrigin() {
        Location origin = map.at(1, 1);

        String message = new BlazeEnchantment().applyTo(List.of(), null, origin);

        assertFalse(containsAFire(origin));
        assertTrue(message.contains("(1, 1)"), message);
    }

    /**
     * @param tile a tile
     * @return true if a Fire item is lying on the tile
     */
    @Test
    void anActorInTheFireIsBurntAndTheBurnIsReportedWithItsTile() throws Exception {
        new StackQueueValleyWorld(new Display()).addGameMap(map);
        Location tile = map.at(1, 1);
        Farmer victim = new Farmer("Victim");
        tile.addActor(victim);
        new BlazeEnchantment().applyTo(List.of(tile), null, map.at(0, 0));
        int healthBefore = victim.getStatistic(ActorStatistics.HEALTH);

        String printed = burnOneTurn(tile, victim);

        assertEquals(healthBefore - 5, victim.getStatistic(ActorStatistics.HEALTH));
        assertTrue(printed.contains("burns for 5 damage at " + tile), printed);
    }

    private boolean containsAFire(Location tile) {
        for (Item item : tile.getItems()) {
            if (item.getClass() == Fire.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * Runs one turn of the fire on a tile and of the statuses on the actor
     * standing there, and returns what was printed.
     *
     * @param tile the burning tile
     * @param victim the actor standing on it
     * @return everything printed during the turn
     */
    private String burnOneTurn(Location tile, Actor victim) {
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(printed, true));
        try {
            tile.tick();
            victim.tickStatuses(tile);
        } finally {
            System.setOut(original);
        }
        return printed.toString();
    }
}
