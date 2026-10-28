package game.pestcontrol;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.pests.SlimeSnail;
import game.positions.area.Area;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the ladybug and the jar that releases it.
 */
class LadybugTest {

    private GameMap map;
    private final Display display = new Display();

    @BeforeEach
    void setUp() {
        FixedRandom.install();
        map = TestFarm.map(",,,,", ",,,,", ",,,,");
        new StackQueueValleyWorld(display).addGameMap(map);
    }

    @AfterEach
    void tearDown() {
        FixedRandom.uninstall();
    }

    @Test
    void eatsAPestNextToIt() throws Exception {
        Ladybug ladybug = new Ladybug();
        SlimeSnail snail = new SlimeSnail();
        map.at(1, 1).addActor(ladybug);
        map.at(2, 1).addActor(snail);

        Action action = ladybug.playTurn(new ActionList(), null, map, display);
        action.execute(ladybug, map);

        assertInstanceOf(EatPestAction.class, action);
        assertFalse(map.contains(snail));
    }

    @Test
    void livesForFiveTurns() throws Exception {
        Ladybug ladybug = new Ladybug();
        map.at(1, 1).addActor(ladybug);

        for (int turn = 1; turn <= 5; turn++) {
            assertTrue(map.contains(ladybug), "alive on turn " + turn);
            ladybug.playTurn(new ActionList(), null, map, display).execute(ladybug, map);
        }
        ladybug.playTurn(new ActionList(), null, map, display);

        assertFalse(map.contains(ladybug));
    }

    @Test
    void aJarReleasesThreeLadybugsAround() {
        Location here = map.at(1, 1);

        String report = new LadybugJar().deterAround(here, map);

        assertEquals(3, countActors(here));
        assertTrue(report.contains("3 ladybugs"), report);
    }

    @Test
    void aJarReleasesOnlyAsManyAsThereIsRoomFor() throws Exception {
        Location here = map.at(1, 1);
        int blocked = 0;
        for (Location tile : Area.adjacentTo(here).tiles()) {
            if (blocked < 6) {
                tile.addActor(new SlimeSnail());
                blocked++;
            }
        }

        String report = new LadybugJar().deterAround(here, map);

        assertEquals(8, countActors(here), "six snails and two ladybugs");
        assertTrue(report.contains("2 ladybugs"), report);
    }

    /**
     * @param here a tile
     * @return how many tiles next to here hold an actor
     */
    private int countActors(Location here) {
        int count = 0;
        for (Location tile : Area.adjacentTo(here).tiles()) {
            if (tile.containsAnActor()) {
                count++;
            }
        }
        return count;
    }
}
