package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests how map tiles are placed on the real world.
 */
class MapGeographyTest {

    /** Small enough to hide rounding in double arithmetic. */
    private static final double TOLERANCE = 1e-9;

    private final GameMap map = TestFarm.map("....", "....", "....");

    @Test
    void theTopLeftTileIsTheOrigin() {
        MapGeography geography = new MapGeography(-37.8, 144.9, 2.0);

        assertEquals(-37.8, geography.latitudeOf(map.at(0, 0)), TOLERANCE);
        assertEquals(144.9, geography.longitudeOf(map.at(0, 0)), TOLERANCE);
    }

    @Test
    void goingRightGoesEastAndGoingDownGoesSouth() {
        MapGeography geography = new MapGeography(-37.8, 144.9, 2.0);

        assertEquals(-41.8, geography.latitudeOf(map.at(3, 2)), TOLERANCE);
        assertEquals(150.9, geography.longitudeOf(map.at(3, 2)), TOLERANCE);
    }

    @Test
    void aLongitudePast180WrapsRoundTheWorld() {
        MapGeography geography = new MapGeography(0, 178.0, 2.0);

        assertEquals(178.0, geography.longitudeOf(map.at(0, 0)), TOLERANCE);
        assertEquals(-180.0, geography.longitudeOf(map.at(1, 0)), TOLERANCE);
        assertEquals(-176.0, geography.longitudeOf(map.at(3, 0)), TOLERANCE);
    }

    @Test
    void aLatitudePastThePoleStopsAtThePole() {
        MapGeography geography = new MapGeography(-88.0, 0, 2.0);

        assertEquals(-90.0, geography.latitudeOf(map.at(0, 2)), TOLERANCE);
    }

    @Test
    void rejectsAnOriginThatIsNotOnEarth() {
        assertRejected(91, 0, 1);
        assertRejected(0, -181, 1);
    }

    @Test
    void rejectsAStepThatIsNotPositive() {
        assertRejected(0, 0, 0);
        assertRejected(0, 0, -1);
        assertRejected(0, 0, Double.NaN);
    }

    /**
     * Checks that a geography with these values cannot be made.
     *
     * @param latitude the origin latitude
     * @param longitude the origin longitude
     * @param step the degrees per tile
     */
    private void assertRejected(double latitude, double longitude, double step) {
        try {
            new MapGeography(latitude, longitude, step);
            fail("Expected the geography to be rejected");
        } catch (IllegalArgumentException expected) {
            // Rejected, as it should be.
        }
    }
}
