package game.pests;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that PestPresent is met only where a pest stands.
 */
class PestPresentTest {

    private GameMap map;
    private final PestPresent pestPresent = new PestPresent();

    @BeforeEach
    void setUp() {
        map = TestFarm.map("...", "...", "...");
        new StackQueueValleyWorld(new Display()).addGameMap(map);
    }

    @Test
    void metWhereAPestStands() throws Exception {
        Location tile = map.at(1, 1);
        tile.addActor(new TestPest());

        assertTrue(pestPresent.isMetBy(tile));
    }

    @Test
    void notMetWhereAnActorThatIsNotAPestStands() throws Exception {
        Location tile = map.at(1, 1);
        tile.addActor(new TestBystander());

        assertFalse(pestPresent.isMetBy(tile));
    }

    @Test
    void notMetOnAnEmptyTile() {
        assertFalse(pestPresent.isMetBy(map.at(1, 1)));
    }

    /** A minimal pest: an actor with the Infestation capability. */
    private static class TestPest extends Actor implements Infestation {

        TestPest() {
            super("Test Pest", 'q', 1);
        }

        @Override
        public String feedOn(Location here) {
            return "";
        }

        @Override
        public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
            return new DoNothingAction();
        }
    }

    /** An actor that is not a pest. */
    private static class TestBystander extends Actor {

        TestBystander() {
            super("Bystander", 'z', 1);
        }

        @Override
        public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
            return new DoNothingAction();
        }
    }
}
