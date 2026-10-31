package game.wanderer;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.StackQueueValleyWorld;
import game.actors.Farmer;
import game.positions.area.AreaFactory;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import game.weather.MapGeography;
import game.weather.WeatherReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests the turn the Wanderer takes: ordinary wandering on most turns, and a
 * weather check every {@value RADIUS_IRRELEVANT} turns that either reacts to
 * the weather or, if the sky cannot be read, reports that and wanders anyway.
 * Also tests that it offers exactly one AskForecastAction to a nearby actor.
 */
class WandererTest {

    /** Documents that this suite does not depend on any reaction's own radius. */
    private static final int RADIUS_IRRELEVANT = 5;

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);
    private final FixedWeatherSource weatherSource = new FixedWeatherSource();
    private final MapGeography mapGeography = new MapGeography(0, 0, 1);
    private final WeatherInterpreter weatherInterpreter = new WeatherInterpreter(areaFactory, random);
    private final Wanderer wanderer =
            new Wanderer(random, weatherSource, mapGeography, weatherInterpreter);

    private GameMap map;
    private final Display display = new Display();

    @BeforeEach
    void setUp() throws Exception {
        map = TestFarm.map("...", "...", "...");
        new StackQueueValleyWorld(display).addGameMap(map);
        map.at(1, 1).addActor(wanderer);
    }

    @Test
    void wandersWithoutCheckingTheWeatherOnTheFirstFourTurns() {
        for (int turn = 1; turn <= 4; turn++) {
            Action action = wanderer.playTurn(new ActionList(), null, map, display);
            assertNotEquals(UnreadableSkyAction.class, action.getClass(), "turn " + turn);
            assertNotEquals(ReactToWeatherAction.class, action.getClass(), "turn " + turn);
        }
    }

    @Test
    void reportsAnUnreadableSkyOnTheFifthTurnWhenNoReadingIsAvailable() {
        Action action = takeFiveTurns();

        assertEquals(UnreadableSkyAction.class, action.getClass());
    }

    @Test
    void reactsToTheWeatherOnTheFifthTurnWhenAReactionApplies() {
        weatherSource.setCurrent(new WeatherReading("Clear", -5.0, 12.0, 0));

        Action action = takeFiveTurns();

        assertEquals(ReactToWeatherAction.class, action.getClass());
    }

    @Test
    void wandersOnTheFifthTurnWhenTheWeatherIsReadButNoReactionApplies() {
        weatherSource.setCurrent(new WeatherReading("Clear", 20.0, 2.0, 0));

        Action action = takeFiveTurns();

        assertNotEquals(UnreadableSkyAction.class, action.getClass());
        assertNotEquals(ReactToWeatherAction.class, action.getClass());
    }

    @Test
    void checksTheWeatherAgainOnlyEveryFiveTurns() {
        weatherSource.setCurrent(new WeatherReading("Clear", -5.0, 12.0, 0));
        takeFiveTurns();

        for (int turn = 6; turn <= 9; turn++) {
            Action action = wanderer.playTurn(new ActionList(), null, map, display);
            assertNotEquals(ReactToWeatherAction.class, action.getClass(), "turn " + turn);
        }
        Action tenthTurn = wanderer.playTurn(new ActionList(), null, map, display);
        assertEquals(ReactToWeatherAction.class, tenthTurn.getClass());
    }

    @Test
    void offersExactlyOneAskForecastActionToANearbyActor() {
        Farmer asker = new Farmer("Test");

        ActionList actions = wanderer.allowableActions(asker, "North", map);

        assertEquals(1, actions.size());
        assertEquals(AskForecastAction.class, actions.get(0).getClass());
    }

    /**
     * Plays 5 turns, the last of which is the one where the Wanderer checks
     * the weather.
     *
     * @return the Action returned on the 5th turn
     */
    private Action takeFiveTurns() {
        Action lastAction = null;
        for (int turn = 1; turn <= 5; turn++) {
            lastAction = wanderer.playTurn(new ActionList(), null, map, display);
        }
        return lastAction;
    }
}
