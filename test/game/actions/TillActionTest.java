package game.actions;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Farmer;
import game.items.tools.Hoe;
import game.positions.farm.Soil;
import game.statistics.StatisticAdjuster;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    private final GameMap map = TestFarm.map("...", "...", "...");

    @Test
    void tillingWithAHoeTurnsDirtIntoSoilAndNamesTheCoordinates() {
        Farmer farmer = new Farmer("Test");
        farmer.getInventory().add(new Hoe(List.of()));
        Location location = map.at(1, 1);
        location.addActor(farmer);

        String message = new TillAction(location).execute(farmer, map);

        assertInstanceOf(Soil.class, location.getGround());
        assertTrue(message.contains("(1, 1)"), message);
    }

    @Test
    void withoutAHoeFailsAndStillNamesTheCoordinates() {
        Farmer farmer = new Farmer("Test");
        Location location = map.at(1, 1);
        location.addActor(farmer);

        String message = new TillAction(location).execute(farmer, map);

        assertTrue(message.contains("(1, 1)"), message);
        assertTrue(message.contains("has no hoe"), message);
    }

    @Test
    void onUntillableGroundFailsAndStillNamesTheCoordinates() {
        Farmer farmer = new Farmer("Test");
        farmer.getInventory().add(new Hoe(List.of()));
        Location location = map.at(1, 1);
        location.setGround(new Soil());
        location.addActor(farmer);

        String message = new TillAction(location).execute(farmer, map);

        assertTrue(message.contains("(1, 1)"), message);
        assertTrue(message.contains("cannot till this ground"), message);
    }

    @Test
    void tooTiredToTillFailsAndStillNamesTheCoordinates() {
        Farmer farmer = new Farmer("Test");
        farmer.getInventory().add(new Hoe(List.of()));
        new StatisticAdjuster(farmer).trySpend(ActorStatistics.STAMINA, 100);
        Location location = map.at(1, 1);
        location.addActor(farmer);

        String message = new TillAction(location).execute(farmer, map);

        assertTrue(message.contains("(1, 1)"), message);
        assertTrue(message.contains("too tired"), message);
    }
}
