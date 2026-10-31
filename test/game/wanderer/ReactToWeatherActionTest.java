package game.wanderer;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Farmer;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests the Action an actor performs to react to the weather: it must
 * delegate to whichever {@link WeatherReaction} it was given, at the
 * acting actor's own Location, and return that reaction's own report.
 * <p>
 * Uses a recording {@link WeatherReaction} test double rather than a real
 * concrete reaction, so this is a pure unit test of the delegation itself.
 */
class ReactToWeatherActionTest {

    private final GameMap map = TestFarm.map(".");
    private final Farmer actor = new Farmer("Test");

    @Test
    void executesTheGivenReactionAtTheActorsOwnLocation() throws Exception {
        map.at(0, 0).addActor(actor);
        RecordingReaction reaction = new RecordingReaction("the reaction's own report");
        ReactToWeatherAction action = new ReactToWeatherAction(reaction);

        String report = action.execute(actor, map);

        assertEquals("the reaction's own report", report);
        assertSame(map.at(0, 0), reaction.whereItWasAsked, "reacted at the actor's own Location");
    }

    @Test
    void menuDescriptionNamesTheActor() {
        ReactToWeatherAction action = new ReactToWeatherAction(new RecordingReaction(""));

        assertEquals(actor + " reacts to the weather", action.menuDescription(actor));
    }

    /**
     * A WeatherReaction that records the Location it was asked to act on,
     * and returns a fixed report.
     */
    private static class RecordingReaction implements WeatherReaction {

        private final String report;
        private Location whereItWasAsked;

        RecordingReaction(String report) {
            this.report = report;
        }

        @Override
        public String reactAround(Location here) {
            whereItWasAsked = here;
            return report;
        }
    }
}
