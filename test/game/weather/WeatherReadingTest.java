package game.weather;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests that a weather reading keeps valid values and rejects invalid ones.
 */
class WeatherReadingTest {

    @Test
    void keepsTheValuesItWasGiven() {
        WeatherReading reading = new WeatherReading("Rain", 12.3, 6.2, 2.1);

        assertEquals("Rain", reading.getCondition());
        assertEquals(12.3, reading.getTemperatureCelsius());
        assertEquals(6.2, reading.getWindSpeedMetresPerSecond());
        assertEquals(2.1, reading.getRainMillimetres());
    }

    @Test
    void describesItselfForTheGameMessages() {
        WeatherReading reading = new WeatherReading("Rain", 12.3, 6.2, 2.1);

        assertEquals("Rain, 12.3°C, wind 6.2 m/s, rain 2.1 mm", reading.toString());
    }

    @Test
    void acceptsCalmDryWeatherBelowFreezing() {
        WeatherReading reading = new WeatherReading("Snow", -15.0, 0.0, 0.0);

        assertEquals(-15.0, reading.getTemperatureCelsius());
        assertEquals(0.0, reading.getWindSpeedMetresPerSecond());
        assertEquals(0.0, reading.getRainMillimetres());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void rejectsABlankCondition(String condition) {
        assertRejected(condition, 10, 1, 0);
    }

    @Test
    void rejectsAMissingCondition() {
        assertRejected(null, 10, 1, 0);
    }

    @Test
    void rejectsNegativeWindOrRain() {
        assertRejected("Clear", 10, -0.1, 0);
        assertRejected("Rain", 10, 1, -0.1);
    }

    @Test
    void rejectsValuesThatAreNotNumbers() {
        assertRejected("Clear", Double.NaN, 1, 0);
        assertRejected("Clear", 10, Double.NaN, 0);
        assertRejected("Clear", 10, 1, Double.NaN);
    }

    /**
     * Checks that a reading with these values cannot be made.
     *
     * @param condition the kind of weather
     * @param temperature the temperature, in degrees Celsius
     * @param wind the wind speed, in metres per second
     * @param rain the rainfall, in millimetres
     */
    private void assertRejected(String condition, double temperature, double wind, double rain) {
        try {
            new WeatherReading(condition, temperature, wind, rain);
            fail("Expected the reading to be rejected");
        } catch (IllegalArgumentException expected) {
            // The reading was rejected, as it should be.
        }
    }
}
