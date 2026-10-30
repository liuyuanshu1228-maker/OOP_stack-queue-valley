package game.plants;

import edu.monash.fit2099.engine.positions.Location;
import game.crops.Cauliflower;
import game.crops.CauliflowerSprout;
import game.crops.Melon;
import game.crops.MelonSprout;
import game.crops.Strawberry;
import game.crops.StrawberrySeedling;
import game.crops.StrawberrySprout;
import game.testsupport.PlantFactory;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that each young plant grows into its next stage at the right age.
 * Dirt is used around the plants so that spreading cannot happen.
 */
class GrowTest extends PlantTestBase {

    static List<Arguments> growthStages() {
        return List.of(
                Arguments.of((PlantFactory) StrawberrySeedling::new, 5, StrawberrySprout.class),
                Arguments.of((PlantFactory) StrawberrySprout::new, 10, Strawberry.class),
                Arguments.of((PlantFactory) CauliflowerSprout::new, 15, Cauliflower.class),
                Arguments.of((PlantFactory) MelonSprout::new, 15, Melon.class));
    }

    private Location plantOnDirt(Plant plant) {
        Location location = TestFarm.map("...", "...", "...").at(1, 1);
        location.setGround(plant);
        return location;
    }

    @ParameterizedTest
    @MethodSource("growthStages")
    void growsIntoTheNextStageAtTheRightAge(PlantFactory stage, int turns, Class<?> nextStage) {
        Plant young = stage.create(random, areaFactory);
        Location location = plantOnDirt(young);

        TestFarm.tick(location, turns - 1);
        assertEquals(young.getClass(), location.getGround().getClass(), "not grown one turn early");

        TestFarm.tick(location, 1);
        assertInstanceOf(nextStage, location.getGround());
        assertEquals(0, ((Plant) location.getGround()).getAge(), "the new stage starts at age 0");
    }

    @ParameterizedTest
    @MethodSource("growthStages")
    void failedGrowthChanceKeepsTheSameStage(PlantFactory stage, int turns, Class<?> nextStage) {
        Plant young = stage.create(random, areaFactory);
        Location location = plantOnDirt(young);
        random.setRollsSucceed(false);

        TestFarm.tick(location, turns * 3);

        assertEquals(young.getClass(), location.getGround().getClass());
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
}
