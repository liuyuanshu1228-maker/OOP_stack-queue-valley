package game.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.util.Optional;

/**
 * Turns the JSON that OpenWeatherMap sends back into a {@link WeatherReading}.
 * <p>
 * Only the values the game uses are read: the kind of weather, the
 * temperature, the wind speed and the rainfall. The kind and the temperature
 * must be there. The wind and the rain may be missing; OpenWeatherMap leaves
 * out the rain when it is dry, so a missing value counts as 0. A reply that
 * cannot be read gives no reading at all, rather than an error.
 */
public class OpenWeatherParser {

    /** The field holding the rainfall over the last hour, in a current reply. */
    private static final String RAIN_LAST_HOUR = "1h";

    /** The field holding the rainfall over three hours, in a forecast entry. */
    private static final String RAIN_THREE_HOURS = "3h";

    /** How many hours a forecast entry's rainfall covers. */
    private static final double FORECAST_RAIN_HOURS = 3.0;

    /**
     * Creates a new OpenWeatherParser.
     */
    public OpenWeatherParser() {
    }

    /**
     * Reads a reply from the current weather endpoint.
     *
     * @param json the reply body
     * @return the current weather, or empty if the reply cannot be read
     */
    public Optional<WeatherReading> parseCurrent(String json) {
        Optional<JsonObject> reply = parseObject(json);
        if (reply.isEmpty()) {
            return Optional.empty();
        }
        return readingFrom(reply.get(), RAIN_LAST_HOUR, 1.0);
    }

    /**
     * Reads a reply from the forecast endpoint, and returns its last entry,
     * which is the one furthest ahead. Its three-hour rainfall is turned into
     * an hourly amount, to match a current reading.
     *
     * @param json the reply body
     * @return the furthest forecast, or empty if the reply cannot be read
     */
    public Optional<WeatherReading> parseLastForecast(String json) {
        Optional<JsonObject> reply = parseObject(json);
        if (reply.isEmpty()) {
            return Optional.empty();
        }
        Optional<JsonObject> lastEntry = lastObjectIn(reply.get(), "list");
        if (lastEntry.isEmpty()) {
            return Optional.empty();
        }
        return readingFrom(lastEntry.get(), RAIN_THREE_HOURS, FORECAST_RAIN_HOURS);
    }

    /**
     * Builds a reading from one set of weather values.
     *
     * @param values the JSON object holding "weather", "main", "wind" and "rain"
     * @param rainField the field inside "rain" that holds the rainfall
     * @param rainHours how many hours that rainfall covers
     * @return the reading, or empty if the kind or the temperature is missing,
     * or a value is out of range
     */
    private Optional<WeatherReading> readingFrom(JsonObject values, String rainField, double rainHours) {
        Optional<String> condition = conditionIn(values);
        Optional<Double> temperature = numberIn(values, "main", "temp");
        if (condition.isEmpty() || temperature.isEmpty()) {
            return Optional.empty();
        }
        double wind = numberOrZero(values, "wind", "speed");
        double rain = numberOrZero(values, "rain", rainField) / rainHours;
        try {
            return Optional.of(new WeatherReading(condition.get(), temperature.get(), wind, rain));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Reads the kind of weather, which OpenWeatherMap puts in the "main"
     * field of the first item of the "weather" list.
     *
     * @param values the JSON object holding the "weather" list
     * @return the kind of weather, or empty if it is missing
     */
    private Optional<String> conditionIn(JsonObject values) {
        JsonElement list = values.get("weather");
        if (list == null || !list.isJsonArray() || list.getAsJsonArray().isEmpty()) {
            return Optional.empty();
        }
        JsonElement first = list.getAsJsonArray().get(0);
        if (!first.isJsonObject()) {
            return Optional.empty();
        }
        JsonElement main = first.getAsJsonObject().get("main");
        if (main == null || !main.isJsonPrimitive() || !main.getAsJsonPrimitive().isString()) {
            return Optional.empty();
        }
        return Optional.of(main.getAsString());
    }

    /**
     * Reads a number held in a field of a nested object, such as "temp" in "main".
     *
     * @param values the JSON object holding the nested object
     * @param objectName the name of the nested object
     * @param field the name of the field inside it
     * @return the number, or empty if the object or the field is missing or not a number
     */
    private Optional<Double> numberIn(JsonObject values, String objectName, String field) {
        JsonElement nested = values.get(objectName);
        if (nested == null || !nested.isJsonObject()) {
            return Optional.empty();
        }
        JsonElement number = nested.getAsJsonObject().get(field);
        if (number == null || !number.isJsonPrimitive() || !number.getAsJsonPrimitive().isNumber()) {
            return Optional.empty();
        }
        return Optional.of(number.getAsDouble());
    }

    /**
     * Reads a number that may be left out, such as the rainfall on a dry day.
     *
     * @param values the JSON object holding the nested object
     * @param objectName the name of the nested object
     * @param field the name of the field inside it
     * @return the number, or 0 if it is missing
     */
    private double numberOrZero(JsonObject values, String objectName, String field) {
        Optional<Double> number = numberIn(values, objectName, field);
        if (number.isEmpty()) {
            return 0.0;
        }
        return number.get();
    }

    /**
     * Finds the last object in a list field.
     *
     * @param values the JSON object holding the list
     * @param listName the name of the list
     * @return the last item, or empty if the list is missing, empty, or its
     * last item is not an object
     */
    private Optional<JsonObject> lastObjectIn(JsonObject values, String listName) {
        JsonElement list = values.get(listName);
        if (list == null || !list.isJsonArray() || list.getAsJsonArray().isEmpty()) {
            return Optional.empty();
        }
        JsonArray items = list.getAsJsonArray();
        JsonElement last = items.get(items.size() - 1);
        if (!last.isJsonObject()) {
            return Optional.empty();
        }
        return Optional.of(last.getAsJsonObject());
    }

    /**
     * Reads a reply body as a JSON object.
     *
     * @param json the reply body
     * @return the object, or empty if the body is missing, not JSON, or not an object
     */
    private Optional<JsonObject> parseObject(String json) {
        if (json == null) {
            return Optional.empty();
        }
        try {
            JsonElement parsed = JsonParser.parseString(json);
            if (!parsed.isJsonObject()) {
                return Optional.empty();
            }
            return Optional.of(parsed.getAsJsonObject());
        } catch (JsonParseException e) {
            return Optional.empty();
        }
    }
}
