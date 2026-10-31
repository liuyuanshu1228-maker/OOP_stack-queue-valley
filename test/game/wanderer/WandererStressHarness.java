package game.wanderer;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.StackQueueValleyWorld;
import game.actors.Farmer;
import game.crops.Strawberry;
import game.crops.StrawberrySeedling;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;
import game.testsupport.TestFarm;
import game.weather.MapGeography;
import game.weather.WeatherReading;
import game.weather.WeatherSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * A manual diagnostic harness, NOT a unit test for marking: plays many
 * independent, randomised games through the Wanderer's full weather
 * interaction (current-weather reactions and forecast omens), to surface a
 * crash that only shows up for some rare combination of random inputs that a
 * handful of hand-picked unit tests would never happen to try.
 * <p>
 * Run it as an ordinary Java application (its own {@code main} method, e.g.
 * right-click it in IntelliJ and choose "Run"). It is deliberately NOT named
 * "...Test", so Maven's surefire plugin never mistakes it for part of
 * {@code mvn test} and never affects marking.
 * <p>
 * Every game uses a seed equal to its own index, so any failure it finds can
 * be reproduced exactly by re-running just that one game with that seed.
 */
public class WandererStressHarness {

    /** How many independent games to play. Lower this for a quicker run. */
    private static final int GAMES_TO_PLAY = 90_000;
    /** How many rounds each game lasts. */
    private static final int ROUNDS_PER_GAME = 500;
    /** The smallest square map side a game is played on. */
    private static final int MIN_MAP_SIDE = 5;
    /** The largest square map side a game is played on. */
    private static final int MAX_MAP_SIDE = 11;
    /** Every this many rounds, the Farmer also asks the Wanderer for the forecast. */
    private static final int ROUNDS_BETWEEN_FORECAST_ASKS = 7;
    /** How often progress is printed, in games played. */
    private static final int PROGRESS_INTERVAL_GAMES = 1_000;
    /** How many of the earliest failures to print in full. */
    private static final int MAX_FAILURES_TO_REPORT = 20;

    public static void main(String[] args) {
        new WandererStressHarness().run();
    }

    /**
     * Plays every game, catching and recording any failure instead of
     * letting it stop the whole run, then prints a summary.
     */
    private void run() {
        List<Failure> failures = new ArrayList<>();
        long startNanos = System.nanoTime();

        for (int game = 0; game < GAMES_TO_PLAY; game++) {
            long seed = game;
            try {
                playOneGame(new Random(seed));
            } catch (Throwable thrown) {
                failures.add(new Failure(game, seed, thrown));
            }
            if ((game + 1) % PROGRESS_INTERVAL_GAMES == 0) {
                long elapsedSeconds = (System.nanoTime() - startNanos) / 1_000_000_000L;
                System.out.println("... " + (game + 1) + "/" + GAMES_TO_PLAY + " games played in "
                        + elapsedSeconds + "s, " + failures.size() + " failures so far");
            }
        }

        reportResults(failures, (System.nanoTime() - startNanos) / 1_000_000L);
    }

    /**
     * Plays a single game: builds a fresh random map, Wanderer and Farmer,
     * then plays {@value #ROUNDS_PER_GAME} rounds, letting the Wanderer act
     * every round, the Farmer ask for the forecast periodically, and the
     * whole map experience the passage of time exactly as the real game loop
     * would (via {@link GameMap#tick()}).
     *
     * @param random this game's own source of randomness, seeded for repeatability
     */
    private void playOneGame(Random random) throws Exception {
        RandomUtils randomUtils = new RandomUtils(random);
        AreaFactory areaFactory = new AreaFactory(randomUtils);
        RandomWeatherSource weatherSource = new RandomWeatherSource(random);
        MapGeography mapGeography = new MapGeography(0, 0, 1);
        WeatherInterpreter weatherInterpreter = new WeatherInterpreter(areaFactory, randomUtils);
        Wanderer wanderer = new Wanderer(randomUtils, weatherSource, mapGeography, weatherInterpreter);
        Farmer farmer = new Farmer("Stress");

        int side = MIN_MAP_SIDE + random.nextInt(MAX_MAP_SIDE - MIN_MAP_SIDE + 1);
        GameMap map = buildRandomMap(side, random, randomUtils, areaFactory);
        new StackQueueValleyWorld(new Display()).addGameMap(map);
        map.at(0, 0).addActor(wanderer);
        map.at(side - 1, side - 1).addActor(farmer);

        AskForecastAction askForecast = new AskForecastAction(wanderer, weatherSource, mapGeography, weatherInterpreter);
        Action lastWandererAction = null;
        for (int round = 0; round < ROUNDS_PER_GAME; round++) {
            lastWandererAction = wanderer.playTurn(new ActionList(), lastWandererAction, map, new Display());
            lastWandererAction.execute(wanderer, map);

            // Mirrors the real game's own gate: Wanderer.allowableActions(...)
            // is only ever called by the engine on a Wanderer that is still
            // on the map, so AskForecastAction is never actually offered to
            // anyone once the Wanderer has died (e.g. burned by its own
            // Campfire) and been removed. Asking unconditionally here would
            // trigger a crash that real gameplay can never reach.
            if (round % ROUNDS_BETWEEN_FORECAST_ASKS == 0 && map.locationOf(wanderer) != null) {
                askForecast.execute(farmer, map);
            }

            map.tick();
        }
    }

    /**
     * Builds a square map of random Dirt/Soil, with a random handful of
     * plants and crops scattered on top, so every WeatherReaction and
     * ForecastOmen has real ground and real plants to act on, not just bare
     * Dirt.
     *
     * @param side the side length of the square map
     * @param random the randomness used to choose ground and plant placement
     * @param randomUtils the randomness handed to each plant created
     * @param areaFactory the AreaFactory handed to each plant created
     * @return the new map
     */
    private GameMap buildRandomMap(int side, Random random, RandomUtils randomUtils, AreaFactory areaFactory) {
        String[] rows = new String[side];
        for (int y = 0; y < side; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < side; x++) {
                row.append(random.nextBoolean() ? ',' : '.');
            }
            rows[y] = row.toString();
        }
        GameMap map = TestFarm.map(rows);

        int plantsToScatter = random.nextInt(side);
        for (int i = 0; i < plantsToScatter; i++) {
            int x = random.nextInt(side);
            int y = random.nextInt(side);
            if (random.nextBoolean()) {
                map.at(x, y).setGround(new StrawberrySeedling(randomUtils, areaFactory));
            } else {
                map.at(x, y).setGround(new Strawberry(randomUtils, areaFactory));
            }
        }
        return map;
    }

    /**
     * Prints how many games ran, how long it took, and the first
     * {@value #MAX_FAILURES_TO_REPORT} failures in full, each with the seed
     * needed to reproduce it.
     *
     * @param failures every failure recorded during the run, in game order
     * @param elapsedMillis how long the whole run took
     */
    private void reportResults(List<Failure> failures, long elapsedMillis) {
        System.out.println();
        System.out.println("Played " + GAMES_TO_PLAY + " games x " + ROUNDS_PER_GAME
                + " rounds in " + elapsedMillis + " ms.");
        System.out.println("Failures: " + failures.size());
        for (int i = 0; i < Math.min(failures.size(), MAX_FAILURES_TO_REPORT); i++) {
            Failure failure = failures.get(i);
            System.out.println();
            System.out.println("Failure #" + (i + 1) + " (game " + failure.game + ", seed " + failure.seed + "):");
            failure.cause.printStackTrace(System.out);
        }
    }

    /** One game's failure, recorded with enough detail to reproduce it. */
    private static class Failure {

        private final int game;
        private final long seed;
        private final Throwable cause;

        Failure(int game, long seed, Throwable cause) {
            this.game = game;
            this.seed = seed;
            this.cause = cause;
        }
    }

    /**
     * A WeatherSource that returns a random reading (or, 1 time in 10, no
     * reading at all, standing for an unreadable sky) on every call, covering
     * both weather conditions WeatherInterpreter recognises and ones it does
     * not, across a realistic range of temperature, wind and rainfall.
     */
    private static class RandomWeatherSource implements WeatherSource {

        /** Out of every 10 calls, this many return no reading at all. */
        private static final int UNREADABLE_CALLS_OUT_OF_TEN = 1;
        private static final String[] CONDITIONS =
                {"Clear", "Rain", "Drizzle", "Thunderstorm", "Snow", "Clouds", "Fog", "Haze"};
        private static final double MIN_TEMPERATURE_CELSIUS = -50.0;
        private static final double MAX_TEMPERATURE_CELSIUS = 50.0;
        private static final double MAX_WIND_SPEED_MPS = 40.0;
        private static final double MAX_RAIN_MM = 30.0;

        private final Random random;

        RandomWeatherSource(Random random) {
            this.random = random;
        }

        @Override
        public Optional<WeatherReading> currentWeatherAt(double latitude, double longitude) {
            return randomReading();
        }

        @Override
        public Optional<WeatherReading> tomorrowWeatherAt(double latitude, double longitude) {
            return randomReading();
        }

        private Optional<WeatherReading> randomReading() {
            if (random.nextInt(10) < UNREADABLE_CALLS_OUT_OF_TEN) {
                return Optional.empty();
            }
            String condition = CONDITIONS[random.nextInt(CONDITIONS.length)];
            double temperature = MIN_TEMPERATURE_CELSIUS
                    + random.nextDouble() * (MAX_TEMPERATURE_CELSIUS - MIN_TEMPERATURE_CELSIUS);
            double windSpeed = random.nextDouble() * MAX_WIND_SPEED_MPS;
            double rain = random.nextDouble() * MAX_RAIN_MM;
            return Optional.of(new WeatherReading(condition, temperature, windSpeed, rain));
        }
    }
}
