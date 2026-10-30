package game.weather;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests reading OpenWeatherMap replies, using fixed JSON text.
 */
class OpenWeatherParserTest {

    /** Small enough to hide rounding in double arithmetic. */
    private static final double TOLERANCE = 1e-9;

    private static final String RAINY_NOW = """
            {"name": "Melbourne",
             "weather": [{"main": "Rain", "description": "light rain"}],
             "main": {"temp": 12.3, "humidity": 81},
             "wind": {"speed": 6.2},
             "rain": {"1h": 2.1}}
            """;

    private static final String FORECAST = """
            {"list": [
              {"weather": [{"main": "Clouds"}], "main": {"temp": 15.0}, "wind": {"speed": 3.0}},
              {"weather": [{"main": "Snow"}], "main": {"temp": -2.5}, "wind": {"speed": 9.0},
               "rain": {"3h": 6.0}}
            ]}
            """;

    private final OpenWeatherParser parser = new OpenWeatherParser();

    @Test
    void readsTheCurrentWeather() {
        WeatherReading reading = parser.parseCurrent(RAINY_NOW).orElseThrow();

        assertEquals("Rain", reading.getCondition());
        assertEquals(12.3, reading.getTemperatureCelsius(), TOLERANCE);
        assertEquals(6.2, reading.getWindSpeedMetresPerSecond(), TOLERANCE);
        assertEquals(2.1, reading.getRainMillimetres(), TOLERANCE);
    }

    @Test
    void missingRainAndWindCountAsZero() {
        String dryAndStill = """
                {"weather": [{"main": "Clear"}], "main": {"temp": 30}}
                """;

        WeatherReading reading = parser.parseCurrent(dryAndStill).orElseThrow();

        assertEquals(0.0, reading.getWindSpeedMetresPerSecond(), TOLERANCE);
        assertEquals(0.0, reading.getRainMillimetres(), TOLERANCE);
    }

    @Test
    void takesTheLastForecastEntryAndMakesItsRainHourly() {
        WeatherReading reading = parser.parseLastForecast(FORECAST).orElseThrow();

        assertEquals("Snow", reading.getCondition());
        assertEquals(-2.5, reading.getTemperatureCelsius(), TOLERANCE);
        assertEquals(2.0, reading.getRainMillimetres(), TOLERANCE);
    }

    @Test
    void aForecastWithOneEntryUsesThatEntry() {
        String single = """
                {"list": [{"weather": [{"main": "Clear"}], "main": {"temp": 20}}]}
                """;

        assertEquals("Clear", parser.parseLastForecast(single).orElseThrow().getCondition());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "not json at all",
            "[1, 2, 3]",
            "{}",
            "{\"weather\": [], \"main\": {\"temp\": 10}}",
            "{\"weather\": [{\"main\": \"Rain\"}]}",
            "{\"weather\": [{\"main\": \"Rain\"}], \"main\": {\"temp\": \"warm\"}}",
            "{\"weather\": [{\"main\": \"Rain\"}], \"main\": {\"temp\": 10}, \"wind\": {\"speed\": -3}}"
    })
    void anUnreadableCurrentReplyGivesNothing(String json) {
        assertTrue(parser.parseCurrent(json).isEmpty(), json);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"list\": []}", "{\"list\": [42]}", "{\"list\": \"none\"}"})
    void anUnreadableForecastGivesNothing(String json) {
        assertTrue(parser.parseLastForecast(json).isEmpty(), json);
    }

    @Test
    void noReplyAtAllGivesNothing() {
        assertEquals(Optional.empty(), parser.parseCurrent(null));
    }
}
