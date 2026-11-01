package game.plants;

import edu.monash.fit2099.engine.positions.Location;
import game.crops.Cauliflower;
import game.crops.CauliflowerSprout;
import game.crops.Melon;
import game.crops.MelonSprout;
import game.crops.Parsnip;
import game.crops.Strawberry;
import game.crops.StrawberrySeedling;
import game.crops.StrawberrySprout;
import game.crops.Tomato;
import game.positions.farm.Dirt;
import game.status.GrowthPausedStatus;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Tests a plant's age, pausing its growth, and regressing it to an earlier stage.
 */
class PlantStateTest extends PlantTestBase {

    private Location plantOnDirt(Plant plant) {
        Location location = TestFarm.map("...", "...", "...").at(1, 1);
        location.setGround(plant);
        return location;
    }

    @Test
    void ageStartsAtZeroAndGoesUpOncePerTurn() {
        Plant plant = new CauliflowerSprout(random, areaFactory);
        Location location = plantOnDirt(plant);
        assertEquals(0, plant.getAge());

        TestFarm.tick(location, 1);
        assertEquals(1, plant.getAge());

        TestFarm.tick(location, 13);
        assertEquals(14, plant.getAge());
    }

    @Test
    void pausedPlantStillAgesButDoesNotGrow() {
        Plant seedling = new StrawberrySeedling(random, areaFactory);
        Location location = plantOnDirt(seedling);
        seedling.addStatus(new GrowthPausedStatus(5));

        TestFarm.tick(location, 5);

        assertInstanceOf(StrawberrySeedling.class, location.getGround(), "turn 5 is skipped");
        assertEquals(5, seedling.getAge());
        assertFalse(seedling.hasStatus(GrowthPausedStatus.class), "the pause has run out");
    }

    @Test
    void plantGrowsOnItsNextTurnAfterThePauseEnds() {
        Plant seedling = new StrawberrySeedling(random, areaFactory);
        Location location = plantOnDirt(seedling);
        seedling.addStatus(new GrowthPausedStatus(5));

        TestFarm.tick(location, 10);

        assertInstanceOf(StrawberrySprout.class, location.getGround());
    }

    @Test
    void aStrawberrySeedlingDiesAndLeavesDirtWhenRegressed() {
        assertRegressesTo(new StrawberrySeedling(random, areaFactory), Dirt.class);
    }

    @Test
    void aStrawberrySproutGoesBackToASeedlingWhenRegressed() {
        assertRegressesTo(new StrawberrySprout(random, areaFactory), StrawberrySeedling.class);
    }

    @Test
    void aStrawberryGoesBackToASproutWhenRegressed() {
        assertRegressesTo(new Strawberry(random, areaFactory), StrawberrySprout.class);
    }

    @Test
    void aCauliflowerSproutDiesAndLeavesDirtWhenRegressed() {
        assertRegressesTo(new CauliflowerSprout(random, areaFactory), Dirt.class);
    }

    @Test
    void aCauliflowerGoesBackToASproutWhenRegressed() {
        assertRegressesTo(new Cauliflower(random, areaFactory), CauliflowerSprout.class);
    }

    @Test
    void aMelonSproutDiesAndLeavesDirtWhenRegressed() {
        assertRegressesTo(new MelonSprout(random, areaFactory), Dirt.class);
    }

    @Test
    void aMelonGoesBackToASproutWhenRegressed() {
        assertRegressesTo(new Melon(random, areaFactory), MelonSprout.class);
    }

    @Test
    void aTomatoDiesAndLeavesDirtWhenRegressed() {
        assertRegressesTo(new Tomato(random, areaFactory), Dirt.class);
    }

    @Test
    void aParsnipDiesAndLeavesDirtWhenRegressed() {
        assertRegressesTo(new Parsnip(random, areaFactory), Dirt.class);
    }

    private void assertRegressesTo(Plant plant, Class<?> earlier) {
        Location location = plantOnDirt(plant);

        location.getGroundAs(Regressible.class).regress(location);

        assertEquals(earlier, location.getGround().getClass());
    }
}
