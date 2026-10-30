package game.testsupport;

import game.plants.Plant;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

/**
 * Builds a {@link Plant} growth stage from the randomness and AreaFactory it
 * needs. Used only to hand a crop constructor (e.g. {@code StrawberrySeedling::new})
 * to a JUnit5 {@code @MethodSource}-fed parameterized test as test data.
 * <p>
 * JUnit5's parameterized tests are idiomatically written with functional
 * interfaces like this one; this is test-harness code, not production game
 * logic, so it sits outside the assignment's SOLID assessment scope. No
 * production class (anything under {@code src/}) implements or references
 * this interface.
 */
@FunctionalInterface
public interface PlantFactory {

    /**
     * Builds a new Plant growth stage.
     *
     * @param randomUtils the randomness handed to the new Plant
     * @param areaFactory the AreaFactory handed to the new Plant
     * @return the new Plant
     */
    Plant create(RandomUtils randomUtils, AreaFactory areaFactory);
}
