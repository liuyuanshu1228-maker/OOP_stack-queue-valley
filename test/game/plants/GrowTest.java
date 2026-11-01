package game.plants;

import edu.monash.fit2099.engine.positions.Location;
import game.crops.Cauliflower;
import game.crops.CauliflowerSprout;
import game.crops.Melon;
import game.crops.MelonSprout;
import game.crops.Strawberry;
import game.crops.StrawberrySeedling;
import game.crops.StrawberrySprout;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that each young plant grows into its next stage at the right age.
 * Dirt is used around the plants so that spreading cannot happen.
 */
class GrowTest extends PlantTestBase {

    private Location plantOnDirt(Plant plant) {
        Location location = TestFarm.map("...", "...", "...").at(1, 1);
        location.setGround(plant);
        return location;
    }

    @Test
    void aStrawberrySeedlingGrowsIntoASproutAfterFiveTurns() {
        assertGrowsAtTheRightAge(new StrawberrySeedling(random, areaFactory), 5, StrawberrySprout.class);
    }

    @Test
    void aStrawberrySproutGrowsIntoAStrawberryAfterTenTurns() {
        assertGrowsAtTheRightAge(new StrawberrySprout(random, areaFactory), 10, Strawberry.class);
    }

    @Test
    void aCauliflowerSproutGrowsIntoACauliflowerAfterFifteenTurns() {
        assertGrowsAtTheRightAge(new CauliflowerSprout(random, areaFactory), 15, Cauliflower.class);
    }

    @Test
    void aMelonSproutGrowsIntoAMelonAfterFifteenTurns() {
        assertGrowsAtTheRightAge(new MelonSprout(random, areaFactory), 15, Melon.class);
    }

    @Test
    void aFailedChanceKeepsTheStrawberrySeedling() {
        assertStaysWhenTheChanceFails(new StrawberrySeedling(random, areaFactory), 5);
    }

    @Test
    void aFailedChanceKeepsTheStrawberrySprout() {
        assertStaysWhenTheChanceFails(new StrawberrySprout(random, areaFactory), 10);
    }

    @Test
    void aFailedChanceKeepsTheCauliflowerSprout() {
        assertStaysWhenTheChanceFails(new CauliflowerSprout(random, areaFactory), 15);
    }

    @Test
    void aFailedChanceKeepsTheMelonSprout() {
        assertStaysWhenTheChanceFails(new MelonSprout(random, areaFactory), 15);
    }

    @Test
    void growthMessageNamesThePlantAndItsCoordinates() {
        Location location = plantOnDirt(new StrawberrySeedling(random, areaFactory));

        TestFarm.tick(location, 5);

        String message = takeOutput();
        assertTrue(message.contains("(1, 1)"), message);
        assertTrue(message.contains("Strawberry Seedling"), message);
        assertTrue(message.contains("Strawberry Sprout"), message);
    }

    @Test
    void seedlingBecomesAStrawberryAfterBothStages() {
        Location location = plantOnDirt(new StrawberrySeedling(random, areaFactory));

        TestFarm.tick(location, 5 + 10);

        assertInstanceOf(Strawberry.class, location.getGround());
    }

    private void assertGrowsAtTheRightAge(Plant young, int turns, Class<?> nextStage) {
        Location location = plantOnDirt(young);

        TestFarm.tick(location, turns - 1);
        assertEquals(young.getClass(), location.getGround().getClass(), "not grown one turn early");

        TestFarm.tick(location, 1);
        assertInstanceOf(nextStage, location.getGround());
        assertEquals(0, ((Plant) location.getGround()).getAge(), "the new stage starts at age 0");
    }

    private void assertStaysWhenTheChanceFails(Plant young, int turns) {
        Location location = plantOnDirt(young);
        random.setRollsSucceed(false);

        TestFarm.tick(location, turns * 3);

        assertEquals(young.getClass(), location.getGround().getClass());
    }
}
