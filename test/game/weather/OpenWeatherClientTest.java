package game.weather;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the requests the client sends, without using the network: a test
 * client returns a fixed reply and records every address it was asked for.
 */
class OpenWeatherClientTest {

    private static final String NAME = "OPENWEATHER_API_KEY";

    private static final String CLEAR_NOW = """
            {"weather": [{"main": "Clear"}], "main": {"temp": 21.5}, "wind": {"speed": 1.0}}
            """;

    @Test
    void asksForTheWeatherAtTheGivenPlace() {
        RecordingClient client = clientWithKey("secret-key", CLEAR_NOW);

        Optional<WeatherReading> reading = client.currentWeatherAt(-37.8136, 144.9631);

        assertEquals("Clear", reading.orElseThrow().getCondition());
        assertEquals(1, client.requested.size());
        String address = client.requested.get(0).toString();
        assertTrue(address.startsWith("https://api.openweathermap.org/data/2.5/weather?"), address);
        assertTrue(address.contains("lat=-37.81&lon=144.96"), address);
        assertTrue(address.contains("units=metric"), address);
        assertTrue(address.endsWith("appid=secret-key"), address);
    }

    @Test
    void asksForNineThreeHourStepsToReachTomorrow() {
        String forecast = "{\"list\": [" + CLEAR_NOW + "]}";
        RecordingClient client = clientWithKey("secret-key", forecast);

        Optional<WeatherReading> reading = client.tomorrowWeatherAt(10, 20);

        assertEquals("Clear", reading.orElseThrow().getCondition());
        String address = client.requested.get(0).toString();
        assertTrue(address.startsWith("https://api.openweathermap.org/data/2.5/forecast?"), address);
        assertTrue(address.contains("cnt=9"), address);
    }

    @Test
    void withoutAKeyNothingIsSent() {
        RecordingClient client = new RecordingClient(noKey(), CLEAR_NOW);

        assertTrue(client.currentWeatherAt(0, 0).isEmpty());
        assertTrue(client.tomorrowWeatherAt(0, 0).isEmpty());
        assertTrue(client.requested.isEmpty());
    }

    @Test
    void aFailedRequestGivesNothing() {
        RecordingClient client = clientWithKey("secret-key", null);

        assertTrue(client.currentWeatherAt(0, 0).isEmpty());
        assertTrue(client.tomorrowWeatherAt(0, 0).isEmpty());
    }

    @Test
    void theKeyIsEncodedInTheAddress() {
        RecordingClient client = clientWithKey("a&b c", CLEAR_NOW);

        client.currentWeatherAt(0, 0);

        assertTrue(client.requested.get(0).toString().endsWith("appid=a%26b+c"));
    }

    /**
     * @param key the API key the client should find
     * @param reply the reply body, or null to make every request fail
     * @return a test client
     */
    private RecordingClient clientWithKey(String key, String reply) {
        return new RecordingClient(new ApiKeyLoader(NAME, Map.of(NAME, key), Path.of("no-such.env")), reply);
    }

    /**
     * @return a loader that finds no key
     */
    private ApiKeyLoader noKey() {
        return new ApiKeyLoader(NAME, Map.of(), Path.of("no-such.env"));
    }

    /**
     * A client that never uses the network: it returns a fixed reply and
     * records each address it was asked to fetch.
     */
    private static class RecordingClient extends OpenWeatherClient {

        private final List<URI> requested = new ArrayList<>();
        private final String reply;

        RecordingClient(ApiKeyLoader keyLoader, String reply) {
            super(keyLoader, new OpenWeatherParser());
            this.reply = reply;
        }

        @Override
        protected Optional<String> fetch(URI address) {
            requested.add(address);
            if (reply == null) {
                return Optional.empty();
            }
            return Optional.of(reply);
        }
    }
}
