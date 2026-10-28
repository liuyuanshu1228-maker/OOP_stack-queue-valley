package game.pestcontrol;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.crops.Strawberry;
import game.pests.SlimeSnail;
import game.positions.farm.Soil;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests what a bag of salt does.
 */
class SaltBarrierTest {

    private GameMap map;
    private Location centre;
    private final SaltBarrier salt = new SaltBarrier();

    @BeforeEach
    void setUp() {
        map = TestFarm.map(",,,,,", ",.,.,", ",,,,,", ",.,.,", ",,,,,");
        new StackQueueValleyWorld(new Display()).addGameMap(map);
        centre = map.at(2, 2);
    }

    @Test
    void saltsTheBareSoilAndDirtAround() {
        salt.deterAround(centre, map);

        assertEquals(8, TestFarm.countGround(map, SaltGround.class));
        assertInstanceOf(Soil.class, centre.getGround(), "the user's own tile is not salted");
    }

    @Test
    void doesNotSaltAPlant() {
        map.at(1, 1).setGround(new Strawberry());

        salt.deterAround(centre, map);

        assertInstanceOf(Strawberry.class, map.at(1, 1).getGround());
        assertEquals(7, TestFarm.countGround(map, SaltGround.class));
    }

    @Test
    void hurtsAPestOnASaltedTileAndPushesItOut() throws Exception {
        SlimeSnail snail = new SlimeSnail();
        map.at(1, 1).addActor(snail);

        String report = salt.deterAround(centre, map);

        Location after = map.locationOf(snail);
        assertNotEquals(map.at(1, 1), after);
        assertNotEquals(SaltGround.class, after.getGround().getClass(), "pushed onto unsalted ground");
        assertTrue(report.contains("burnt"), report);
    }

    @Test
    void aMapCornerHasOnlyThreeTilesToSalt() {
        String report = salt.deterAround(map.at(0, 0), map);

        assertEquals(3, TestFarm.countGround(map, SaltGround.class));
        assertTrue(report.contains("3 tiles"), report);
    }
}
