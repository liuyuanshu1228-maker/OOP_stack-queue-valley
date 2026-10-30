package game.weather;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

/**
 * Asks OpenWeatherMap for the real weather.
 * <p>
 * The API key is found by an {@link ApiKeyLoader}, so it is never written in
 * the code. If there is no key, or a request fails or times out, the answer is
 * empty and the game carries on without the weather. The reply is read by an
 * {@link OpenWeatherParser}.
 * <p>
 * Sending the request is kept in {@link #fetch(URI)}, which a test can
 * override to return a fixed reply, so no test needs the network.
 */
public class OpenWeatherClient implements WeatherSource {

    /** The endpoint for the weather right now. */
    private static final String CURRENT_URL = "https://api.openweathermap.org/data/2.5/weather";

    /** The endpoint for the forecast, given in three-hour steps. */
    private static final String FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast";

    /** Nine three-hour steps: the last one is about 24 hours ahead. */
    private static final int FORECAST_STEPS_TO_TOMORROW = 9;

    /** How long to wait for a reply before giving up, so the game never hangs. */
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    /** The HTTP status code of a successful reply. */
    private static final int HTTP_OK = 200;

    /** The API key, or an empty string if there is none. */
    private final String apiKey;

    /** Reads the replies. */
    private final OpenWeatherParser parser;

    /** Sends the requests. */
    private final HttpClient httpClient;

    /**
     * Creates a client.
     *
     * @param keyLoader finds the API key
     * @param parser reads the replies
     */
    public OpenWeatherClient(ApiKeyLoader keyLoader, OpenWeatherParser parser) {
        Optional<String> key = keyLoader.load();
        this.apiKey = key.isPresent() ? key.get() : "";
        this.parser = parser;
        this.httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    }

    /**
     * Asks for the weather at a place right now.
     *
     * @param latitude the latitude of the place
     * @param longitude the longitude of the place
     * @return the current weather, or empty if there is no key or the request fails
     */
    @Override
    public Optional<WeatherReading> currentWeatherAt(double latitude, double longitude) {
        Optional<String> reply = request(CURRENT_URL, latitude, longitude, "");
        if (reply.isEmpty()) {
            return Optional.empty();
        }
        return parser.parseCurrent(reply.get());
    }

    /**
     * Asks for the forecast at a place for about this time tomorrow.
     *
     * @param latitude the latitude of the place
     * @param longitude the longitude of the place
     * @return tomorrow's forecast, or empty if there is no key or the request fails
     */
    @Override
    public Optional<WeatherReading> tomorrowWeatherAt(double latitude, double longitude) {
        Optional<String> reply = request(FORECAST_URL, latitude, longitude,
                "&cnt=" + FORECAST_STEPS_TO_TOMORROW);
        if (reply.isEmpty()) {
            return Optional.empty();
        }
        return parser.parseLastForecast(reply.get());
    }

    /**
     * Sends a request to one endpoint, unless there is no key.
     *
     * @param endpoint the endpoint's address
     * @param latitude the latitude of the place
     * @param longitude the longitude of the place
     * @param extraQuery any further query parameters, starting with "&amp;", or ""
     * @return the reply body, or empty if there is no key or the request fails
     */
    private Optional<String> request(String endpoint, double latitude, double longitude, String extraQuery) {
        if (apiKey.isEmpty()) {
            return Optional.empty();
        }
        String address = String.format(Locale.ROOT, "%s?lat=%.2f&lon=%.2f&units=metric%s&appid=%s",
                endpoint, latitude, longitude, extraQuery,
                URLEncoder.encode(apiKey, StandardCharsets.UTF_8));
        return fetch(URI.create(address));
    }

    /**
     * Sends one GET request and returns the body of a successful reply.
     * Protected so that a test can return a fixed reply instead of using the network.
     *
     * @param address the full request address, including the key
     * @return the reply body, or empty if the request fails, times out or is refused
     */
    protected Optional<String> fetch(URI address) {
        HttpRequest request = HttpRequest.newBuilder(address).timeout(TIMEOUT).GET().build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HTTP_OK) {
                return Optional.empty();
            }
            return Optional.of(response.body());
        } catch (IOException e) {
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }
}
