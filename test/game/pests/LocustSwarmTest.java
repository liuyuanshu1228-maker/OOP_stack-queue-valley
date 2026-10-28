package game.pests;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.crops.Strawberry;
import game.crops.StrawberrySeedling;
import game.crops.StrawberrySprout;
import game.positions.area.Area;
import game.positions.farm.Soil;
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
 * Tests what a LocustSwarm does when it feeds.
 */
class LocustSwarmTest {

    private GameMap map;
    private Location centre;
    private FixedRandom random;
    private final LocustSwarm swarm = new LocustSwarm();

    @BeforeEach
    void setUp() throws Exception {
        random = FixedRandom.install();
        random.setRollsSucceed(false);
        map = TestFarm.map(",,,", ",,,", ",,,");
        new StackQueueValleyWorld(new Display()).addGameMap(map);
        centre = map.at(1, 1);
        centre.addActor(swarm);
    }

    @AfterEach
    void tearDown() {
        FixedRandom.uninstall();
    }

    @Test
    void setsEveryPlantOnAndAroundItBackOneStage() {
        centre.setGround(new Strawberry());
        map.at(0, 0).setGround(new Strawberry());
        map.at(2, 2).setGround(new StrawberrySeedling());

        String report = swarm.feedOn(centre);

        assertInstanceOf(StrawberrySprout.class, centre.getGround());
        assertInstanceOf(StrawberrySprout.class, map.at(0, 0).getGround());
        assertInstanceOf(Soil.class, map.at(2, 2).getGround());
        assertTrue(report.contains("descends"), report);
    }

    @Test
    void canFeedOnAPlantNextToItWithoutStandingOnOne() {
        map.at(2, 1).setGround(new Strawberry());

        assertTrue(swarm.canFeedAt(centre));
        swarm.feedOn(centre);

        assertInstanceOf(StrawberrySprout.class, map.at(2, 1).getGround());
    }

    @Test
    void doesNothingWhereThereIsNoPlantAround() {
        assertFalse(swarm.canFeedAt(centre));
        assertEquals("", swarm.feedOn(centre));
        assertTrue(centre.containsAnActor(), "a swarm that did not eat stays");
    }

    @Test
    void fliesAwayAfterItsLastMeal() {
        for (int meal = 1; meal <= 4; meal++) {
            assertTrue(centre.containsAnActor(), "still here before meal " + meal);
            centre.setGround(new Strawberry());
            swarm.feedOn(centre);
        }

        assertFalse(centre.containsAnActor());
    }

    @Test
    void splitsIntoASmallerSwarmWhenTheChanceRolls() {
        random.setRollsSucceed(true);
        centre.setGround(new Strawberry());

        String report = swarm.feedOn(centre);

        assertEquals(2, countSwarms());
        assertTrue(report.contains("splits"), report);
    }

    @Test
    void theSmallerSwarmFliesAwayAfterOneMeal() {
        random.setRollsSucceed(true);
        centre.setGround(new Strawberry());
        swarm.feedOn(centre);
        random.setRollsSucceed(false);
        Location offshootTile = offshootTile();

        offshootTile.setGround(new Strawberry());
        Infestation offshoot = offshootTile.getActor().asCapability(Infestation.class).orElseThrow();
        offshoot.feedOn(offshootTile);

        assertFalse(offshootTile.containsAnActor());
    }

    @Test
    void doesNotSplitWhenThereIsNoRoomAroundIt() throws Exception {
        random.setRollsSucceed(true);
        for (Location tile : Area.adjacentTo(centre).tiles()) {
            tile.addActor(new LocustSwarm());
        }
        centre.setGround(new Strawberry());

        String report = swarm.feedOn(centre);

        assertEquals(9, countSwarms());
        assertFalse(report.contains("splits"), report);
    }

    /**
     * @return how many tiles of the map hold an actor
     */
    private int countSwarms() {
        int count = 0;
        for (Location tile : Area.centredOn(centre).tiles()) {
            if (tile.containsAnActor()) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return the tile next to the centre that holds the split-off swarm
     */
    private Location offshootTile() {
        for (Location tile : Area.adjacentTo(centre).tiles()) {
            if (tile.containsAnActor()) {
                return tile;
            }
        }
        throw new AssertionError("no swarm split off");
    }
}
