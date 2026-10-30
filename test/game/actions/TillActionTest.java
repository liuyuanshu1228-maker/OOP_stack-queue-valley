package game.actions;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.actors.Farmer;
import game.items.tools.Hoe;
import game.positions.area.AreaFactory;
import game.positions.farm.Soil;
import game.statistics.StatisticAdjuster;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that TillAction's outcome message names its Location on every
 * branch -- success and every failure alike -- not only the success path.
 * This is what {@link CapabilityToolAction}'s shared {@code at(...)} helper
 * is responsible for, so these tests exercise it through TillAction, one of
 * its subclasses.
 */
class TillActionTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);

    private final GameMap map = TestFarm.map("...", "...", "...");

    @BeforeEach
    void registerMap() {
        new StackQueueValleyWorld(new Display()).addGameMap(map);
    }

    @Test
    void tillingWithAHoeTurnsDirtIntoSoilAndNamesTheCoordinates() throws Exception {
        Farmer farmer = new Farmer("Test");
        farmer.getInventory().add(new Hoe(List.of(), areaFactory));
        Location location = map.at(1, 1);
        location.addActor(farmer);

        String message = new TillAction(location).execute(farmer, map);

        assertInstanceOf(Soil.class, location.getGround());
        assertTrue(message.contains("(1, 1)"), message);
    }

    @Test
    void withoutAHoeFailsAndStillNamesTheCoordinates() throws Exception {
        Farmer farmer = new Farmer("Test");
        Location location = map.at(1, 1);
        location.addActor(farmer);

        String message = new TillAction(location).execute(farmer, map);

        assertTrue(message.contains("(1, 1)"), message);
        assertTrue(message.contains("has no hoe"), message);
    }

    @Test
    void onUntillableGroundFailsAndStillNamesTheCoordinates() throws Exception {
        Farmer farmer = new Farmer("Test");
        farmer.getInventory().add(new Hoe(List.of(), areaFactory));
        Location location = map.at(1, 1);
        location.setGround(new Soil());
        location.addActor(farmer);

        String message = new TillAction(location).execute(farmer, map);

        assertTrue(message.contains("(1, 1)"), message);
        assertTrue(message.contains("cannot till this ground"), message);
    }

    @Test
    void tooTiredToTillFailsAndStillNamesTheCoordinates() throws Exception {
        Farmer farmer = new Farmer("Test");
        farmer.getInventory().add(new Hoe(List.of(), areaFactory));
        new StatisticAdjuster(farmer).trySpend(ActorStatistics.STAMINA, 100);
        Location location = map.at(1, 1);
        location.addActor(farmer);

        String message = new TillAction(location).execute(farmer, map);

        assertTrue(message.contains("(1, 1)"), message);
        assertTrue(message.contains("too tired"), message);
    }
}
