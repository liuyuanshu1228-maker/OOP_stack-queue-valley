package game.wanderer;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Farmer;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the Action that wraps the Wanderer's ordinary wandering Action for a
 * turn when the current weather could not be read: it must report the
 * unreadable sky, then still carry out the wrapped wandering Action, and its
 * menu description must be exactly the wrapped Action's own.
 * <p>
 * Uses a fixed-report wandering {@link Action} test double rather than a
 * real {@code MoveActorAction}, so this is a pure unit test of the wrapping
 * itself.
 */
class UnreadableSkyActionTest {

    private final GameMap map = TestFarm.map(".");
    private final Farmer actor = new Farmer("Test");

    @Test
    void reportsTheUnreadableSkyThenTheWrappedWanderingOutcome() {
        FixedReportAction wandering = new FixedReportAction("wandered north", "wander north");
        UnreadableSkyAction action = new UnreadableSkyAction(wandering);

        String report = action.execute(actor, map);

        assertEquals("The sky is unreadable, and the Wanderer only wanders. wandered north", report);
    }

    @Test
    void menuDescriptionIsExactlyTheWrappedActionsOwn() {
        FixedReportAction wandering = new FixedReportAction("wandered north", "wander north");
        UnreadableSkyAction action = new UnreadableSkyAction(wandering);

        assertEquals("wander north", action.menuDescription(actor));
    }

    /** An Action that always returns the same fixed execute report and menu description. */
    private static class FixedReportAction extends Action {

        private final String executeReport;
        private final String menuDescription;

        FixedReportAction(String executeReport, String menuDescription) {
            this.executeReport = executeReport;
            this.menuDescription = menuDescription;
        }

        @Override
        public String execute(Actor actor, GameMap map) {
            return executeReport;
        }

        @Override
        public String menuDescription(Actor actor) {
            return menuDescription;
        }
    }
}
