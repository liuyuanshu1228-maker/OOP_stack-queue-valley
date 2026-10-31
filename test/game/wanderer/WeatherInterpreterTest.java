package game.wanderer;

import game.positions.area.AreaFactory;
import game.testsupport.FixedRandom;
import game.weather.WeatherReading;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the priority order {@link WeatherInterpreter} checks its thresholds
 * in: {@code reactionFor} tries a strong wind, then rain, then cold;
 * {@code omenFor} tries frost, then rain, then heat. Each test checks the
 * concrete class produced with {@code getClass()}, never {@code instanceof}.
 */
class WeatherInterpreterTest {

    private final WeatherInterpreter interpreter =
            new WeatherInterpreter(new AreaFactory(new FixedRandom()), new FixedRandom());

    @Test
    void strongWindTakesPriorityOverRain() {
        WeatherReading stormyAndRainy = new WeatherReading("Rain", 15.0, 12.0, 3.0);

        Optional<WeatherReaction> reaction = interpreter.reactionFor(stormyAndRainy);

        assertEquals(StormShelterReaction.class, reaction.orElseThrow().getClass());
    }

    @Test
    void everyRainyConditionTriggersRainDanceWhenTheWindIsCalm() {
        for (String rainyCondition : new String[] {"Rain", "Drizzle", "Thunderstorm"}) {
            WeatherReading reading = new WeatherReading(rainyCondition, 15.0, 2.0, 1.0);

            Optional<WeatherReaction> reaction = interpreter.reactionFor(reading);

            assertEquals(RainDanceReaction.class, reaction.orElseThrow().getClass(), rainyCondition);
        }
    }

    @Test
    void coldWeatherTriggersCampfireWhenNotStormyOrRainy() {
        WeatherReading cold = new WeatherReading("Clear", 5.0, 2.0, 0);

        Optional<WeatherReaction> reaction = interpreter.reactionFor(cold);

        assertEquals(CampfireReaction.class, reaction.orElseThrow().getClass());
    }

    @Test
    void mildCalmDryWeatherTriggersNoReaction() {
        WeatherReading mild = new WeatherReading("Clear", 20.0, 2.0, 0);

        assertTrue(interpreter.reactionFor(mild).isEmpty());
    }

    @Test
    void snowAlwaysTriggersFrostOmenRegardlessOfTemperature() {
        WeatherReading warmSnow = new WeatherReading("Snow", 10.0, 0, 0);

        Optional<ForecastOmen> omen = interpreter.omenFor(warmSnow);

        assertEquals(FrostOmen.class, omen.orElseThrow().getClass());
    }

    @Test
    void coldEnoughTriggersFrostOmenEvenWithoutSnow() {
        WeatherReading cold = new WeatherReading("Clear", 2.0, 0, 0);

        Optional<ForecastOmen> omen = interpreter.omenFor(cold);

        assertEquals(FrostOmen.class, omen.orElseThrow().getClass());
    }

    @Test
    void frostOmenTakesPriorityOverSowingOmen() {
        WeatherReading coldRain = new WeatherReading("Rain", 1.0, 0, 1.0);

        Optional<ForecastOmen> omen = interpreter.omenFor(coldRain);

        assertEquals(FrostOmen.class, omen.orElseThrow().getClass());
    }

    @Test
    void rainyForecastTriggersSowingOmenWhenNotFrosty() {
        WeatherReading rain = new WeatherReading("Rain", 15.0, 0, 1.0);

        Optional<ForecastOmen> omen = interpreter.omenFor(rain);

        assertEquals(SowingOmen.class, omen.orElseThrow().getClass());
    }

    @Test
    void hotClearForecastTriggersDroughtOmen() {
        WeatherReading hot = new WeatherReading("Clear", 25.0, 0, 0);

        Optional<ForecastOmen> omen = interpreter.omenFor(hot);

        assertEquals(DroughtOmen.class, omen.orElseThrow().getClass());
    }

    @Test
    void mildClearForecastTriggersNoOmen() {
        WeatherReading mild = new WeatherReading("Clear", 15.0, 0, 0);

        assertTrue(interpreter.omenFor(mild).isEmpty());
    }
}
