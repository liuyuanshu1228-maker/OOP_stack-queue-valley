package game.pestcontrol;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.pests.SlimeSnail;
import game.positions.farm.Soil;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests what a set pest trap does each turn.
 */
class TrapGroundTest {

    private GameMap map;
    private Location trapTile;

    @BeforeEach
    void setUp() {
        map = TestFarm.map(",,,,,", ",,,,,", ",,,,,", ",,,,,", ",,,,,");
        new StackQueueValleyWorld(new Display()).addGameMap(map);
        trapTile = map.at(2, 2);
        trapTile.setGround(new TrapGround(trapTile.getGround()));
    }

    @Test
    void capturesAPestNextToIt() throws Exception {
        SlimeSnail snail = new SlimeSnail();
        map.at(3, 2).addActor(snail);

        TestFarm.tick(trapTile, 1);

        assertFalse(map.contains(snail));
    }

    @Test
    void pullsAPestTwoTilesAwayAndCapturesItInTheSameTurn() throws Exception {
        SlimeSnail snail = new SlimeSnail();
        map.at(4, 0).addActor(snail);

        TestFarm.tick(trapTile, 1);

        assertFalse(map.contains(snail), "pulled next to the trap, then captured");
    }

    @Test
    void cannotPullAPestWhoseWayIsBlocked() throws Exception {
        SlimeSnail snail = new SlimeSnail();
        map.at(4, 0).addActor(snail);
        map.at(3, 1).addActor(new Ladybug());

        TestFarm.tick(trapTile, 1);

        assertEquals(map.at(4, 0), map.locationOf(snail));
    }

    @Test
    void leavesAPestThreeTilesAwayAlone() throws Exception {
        map = TestFarm.map(",,,,,,", ",,,,,,", ",,,,,,");
        new StackQueueValleyWorld(new Display()).addGameMap(map);
        trapTile = map.at(1, 1);
        trapTile.setGround(new TrapGround(trapTile.getGround()));
        SlimeSnail snail = new SlimeSnail();
        map.at(4, 1).addActor(snail);

        TestFarm.tick(trapTile, 1);

        assertEquals(map.at(4, 1), map.locationOf(snail));
    }

    @Test
    void isUsedUpAfterTwoCaptures() throws Exception {
        SlimeSnail[] snails = {new SlimeSnail(), new SlimeSnail(), new SlimeSnail()};
        map.at(1, 1).addActor(snails[0]);
        map.at(3, 3).addActor(snails[1]);
        map.at(3, 2).addActor(snails[2]);

        TestFarm.tick(trapTile, 1);

        int left = 0;
        for (SlimeSnail snail : snails) {
            if (map.contains(snail)) {
                left++;
            }
        }
        assertEquals(1, left, "a used-up trap captures no more");
        assertInstanceOf(Soil.class, trapTile.getGround());
    }

    @Test
    void leavesActorsThatAreNotPestsAlone() throws Exception {
        Ladybug ladybug = new Ladybug();
        map.at(3, 2).addActor(ladybug);

        TestFarm.tick(trapTile, 1);

        assertTrue(map.contains(ladybug));
    }

    @Test
    void turnsBackWhenItExpires() {
        TestFarm.tick(trapTile, 10);

        assertInstanceOf(Soil.class, trapTile.getGround());
    }
}
