package game.plants;

import edu.monash.fit2099.engine.positions.Location;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests that each plant ability only acts on a plant with the matching
 * capability ({@link Growing}, {@link Spreading} or {@link Producing}), and
 * leaves any other plant alone instead of failing.
 */
class PlantCapabilityTest extends PlantTestBase {

    /** A plant with no capabilities, used to check that abilities leave it alone. */
    private static class BarePlant extends Plant {

        /**
         * Creates a bare plant with the given abilities.
         *
         * @param abilities the abilities of this plant
         */
        BarePlant(PlantAbility... abilities) {
            super('?', "Bare plant", abilities);
        }
    }

    /**
     * Places a plant in the middle of a 3x3 patch of Soil, so it has room to
     * spread and drop items.
     *
     * @param plant the plant to place
     * @return the location of the plant
     */
    private Location plantOnSoil(Plant plant) {
        Location location = TestFarm.map(",,,", ",,,", ",,,").at(1, 1);
        location.setGround(plant);
        return location;
    }

    @Test
    void growLeavesAPlantThatIsNotGrowing() {
        Plant plant = new BarePlant(new Grow(1, 100, random));
        Location location = plantOnSoil(plant);

        TestFarm.tick(location, 5);

        assertSame(plant, location.getGround());
        assertEquals("", takeOutput());
    }

    @Test
    void selfReplicateLeavesAPlantThatIsNotSpreading() {
        Plant plant = new BarePlant(new SelfReplicate(1, random, areaFactory));
        Location location = plantOnSoil(plant);

        TestFarm.tick(location, 5);

        assertEquals(1, TestFarm.countGround(location.map(), BarePlant.class));
        assertEquals("", takeOutput());
    }

    @Test
    void produceLeavesAPlantThatIsNotProducing() {
        Plant plant = new BarePlant(new Produce(1, random, areaFactory));
        Location location = plantOnSoil(plant);

        TestFarm.tick(location, 5);

        assertEquals(0, TestFarm.countItems(location.map()));
        assertEquals("", takeOutput());
    }
}
