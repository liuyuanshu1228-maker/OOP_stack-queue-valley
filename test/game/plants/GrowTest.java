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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that each young plant grows into its next stage at the right age.
 * Dirt is used around the plants so that spreading cannot happen.
 */
class GrowTest extends PlantTestBase {

    static Stream<Arguments> growthStages() {
        return Stream.of(
                Arguments.of((Supplier<Plant>) StrawberrySeedling::new, 5, StrawberrySprout.class),
                Arguments.of((Supplier<Plant>) StrawberrySprout::new, 10, Strawberry.class),
                Arguments.of((Supplier<Plant>) CauliflowerSprout::new, 15, Cauliflower.class),
                Arguments.of((Supplier<Plant>) MelonSprout::new, 15, Melon.class));
    }

    private Location plantOnDirt(Plant plant) {
        Location location = TestFarm.map("...", "...", "...").at(1, 1);
        location.setGround(plant);
        return location;
    }

    @ParameterizedTest
    @MethodSource("growthStages")
    void growsIntoTheNextStageAtTheRightAge(Supplier<Plant> stage, int turns, Class<?> nextStage) {
        Plant young = stage.get();
        Location location = plantOnDirt(young);

        TestFarm.tick(location, turns - 1);
        assertEquals(young.getClass(), location.getGround().getClass(), "not grown one turn early");

        TestFarm.tick(location, 1);
        assertInstanceOf(nextStage, location.getGround());
        assertEquals(0, ((Plant) location.getGround()).getAge(), "the new stage starts at age 0");
    }

    @ParameterizedTest
    @MethodSource("growthStages")
    void failedGrowthChanceKeepsTheSameStage(Supplier<Plant> stage, int turns, Class<?> nextStage) {
        Plant young = stage.get();
        Location location = plantOnDirt(young);
        random.setRollsSucceed(false);

        TestFarm.tick(location, turns * 3);

        assertEquals(young.getClass(), location.getGround().getClass());
    }

    @Test
    void growthMessageNamesThePlantAndItsCoordinates() {
        Location location = plantOnDirt(new StrawberrySeedling());

        TestFarm.tick(location, 5);

        String message = takeOutput();
        assertTrue(message.contains("(1, 1)"), message);
        assertTrue(message.contains("Strawberry Seedling"), message);
        assertTrue(message.contains("Strawberry Sprout"), message);
    }

    @Test
    void seedlingBecomesAStrawberryAfterBothStages() {
        Location location = plantOnDirt(new StrawberrySeedling());

        TestFarm.tick(location, 5 + 10);

        assertInstanceOf(Strawberry.class, location.getGround());
    }
}
