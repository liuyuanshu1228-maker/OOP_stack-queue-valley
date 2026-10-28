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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Tests a plant's age, pausing its growth, and regressing it to an earlier stage.
 */
class PlantStateTest extends PlantTestBase {

    static List<Arguments> earlierStages() {
        return List.of(
                Arguments.of((Supplier<Plant>) StrawberrySeedling::new, Dirt.class),
                Arguments.of((Supplier<Plant>) StrawberrySprout::new, StrawberrySeedling.class),
                Arguments.of((Supplier<Plant>) Strawberry::new, StrawberrySprout.class),
                Arguments.of((Supplier<Plant>) CauliflowerSprout::new, Dirt.class),
                Arguments.of((Supplier<Plant>) Cauliflower::new, CauliflowerSprout.class),
                Arguments.of((Supplier<Plant>) MelonSprout::new, Dirt.class),
                Arguments.of((Supplier<Plant>) Melon::new, MelonSprout.class),
                Arguments.of((Supplier<Plant>) Tomato::new, Dirt.class),
                Arguments.of((Supplier<Plant>) Parsnip::new, Dirt.class));
    }

    private Location plantOnDirt(Plant plant) {
        Location location = TestFarm.map("...", "...", "...").at(1, 1);
        location.setGround(plant);
        return location;
    }

    @Test
    void ageStartsAtZeroAndGoesUpOncePerTurn() {
        Plant plant = new CauliflowerSprout();
        Location location = plantOnDirt(plant);
        assertEquals(0, plant.getAge());

        TestFarm.tick(location, 1);
        assertEquals(1, plant.getAge());

        TestFarm.tick(location, 13);
        assertEquals(14, plant.getAge());
    }

    @Test
    void pausedPlantStillAgesButDoesNotGrow() {
        Plant seedling = new StrawberrySeedling();
        Location location = plantOnDirt(seedling);
        seedling.addStatus(new GrowthPausedStatus(5));

        TestFarm.tick(location, 5);

        assertInstanceOf(StrawberrySeedling.class, location.getGround(), "turn 5 is skipped");
        assertEquals(5, seedling.getAge());
        assertFalse(seedling.hasStatus(GrowthPausedStatus.class), "the pause has run out");
    }

    @Test
    void plantGrowsOnItsNextTurnAfterThePauseEnds() {
        Plant seedling = new StrawberrySeedling();
        Location location = plantOnDirt(seedling);
        seedling.addStatus(new GrowthPausedStatus(5));

        TestFarm.tick(location, 10);

        assertInstanceOf(StrawberrySprout.class, location.getGround());
    }

    @ParameterizedTest
    @MethodSource("earlierStages")
    void regressGoesBackToTheEarlierStage(Supplier<Plant> stage, Class<?> earlier) {
        Location location = plantOnDirt(stage.get());

        location.getGroundAs(Regressible.class).regress(location);

        assertEquals(earlier, location.getGround().getClass());
    }
}
