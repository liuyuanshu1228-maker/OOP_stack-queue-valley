package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.actors.Farmer;
import game.random.RandomUtils;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the random step that the Wanderer, the pests' fallback, the ladybugs,
 * the animals, the Merchant and the perching crow all share. The random pick
 * is fixed with {@link FixedRandom} (always the first option) or with
 * {@link PickIndex} (a chosen option), so every test is repeatable.
 */
class WanderBehaviourTest {

    private GameMap map;
    private final Farmer walker = new Farmer("Walker");

    @BeforeEach
    void setUp() {
        map = TestFarm.map("...", "...", "...");
        new StackQueueValleyWorld(new Display()).addGameMap(map);
    }

    @Test
    void withRoomAroundItStepsOntoANeighbouringTile() throws Exception {
        Location start = map.at(1, 1);
        start.addActor(walker);

        Action step = new WanderBehaviour(new FixedRandom()).operate(walker, start);
        step.execute(walker, map);

        Location after = map.locationOf(walker);
        assertNotEquals(start, after);
        assertTrue(isNextTo(start, after), "moved one step, to " + after);
    }

    @Test
    void stepsOnlyOntoTheOneTileItCanEnter() throws Exception {
        Location start = map.at(1, 1);
        start.addActor(walker);
        wallOffAllButOne(start, map.at(2, 2));

        Action step = new WanderBehaviour(new FixedRandom()).operate(walker, start);
        step.execute(walker, map);

        assertSame(map.at(2, 2), map.locationOf(walker));
    }

    @Test
    void aTileWithAnotherActorOnItIsSkipped() throws Exception {
        Location start = map.at(1, 1);
        start.addActor(walker);
        for (Location tile : neighboursOf(start)) {
            if (tile != map.at(0, 2)) {
                tile.addActor(new Farmer("Bystander"));
            }
        }

        Action step = new WanderBehaviour(new FixedRandom()).operate(walker, start);
        step.execute(walker, map);

        assertSame(map.at(0, 2), map.locationOf(walker));
    }

    @Test
    void whenNoNeighbourCanBeEnteredItGivesNoAction() throws Exception {
        Location start = map.at(1, 1);
        start.addActor(walker);
        wallOffAllButOne(start, null);

        assertNull(new WanderBehaviour(new FixedRandom()).operate(walker, start));
    }

    @Test
    void inACornerEachOfTheThreeNeighboursCanBeChosen() throws Exception {
        Location corner = map.at(0, 0);
        corner.addActor(walker);

        Set<Location> reached = new HashSet<>();
        for (int index = 0; index < 3; index++) {
            Action step = new WanderBehaviour(new PickIndex(index)).operate(walker, corner);
            step.execute(walker, map);
            reached.add(map.locationOf(walker));
            map.moveActor(walker, corner);
        }

        assertEquals(Set.of(map.at(1, 0), map.at(0, 1), map.at(1, 1)), reached);
    }

    @Test
    void theMenuTextNamesTheDestinationCoordinates() throws Exception {
        Location start = map.at(1, 1);
        start.addActor(walker);
        wallOffAllButOne(start, map.at(2, 1));

        Action step = new WanderBehaviour(new FixedRandom()).operate(walker, start);

        assertTrue(step.menuDescription(walker).contains("(2, 1)"), step.menuDescription(walker));
    }

    private Set<Location> neighboursOf(Location centre) {
        Set<Location> neighbours = new HashSet<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int x = centre.x() + dx;
                int y = centre.y() + dy;
                boolean onMap = map.getXRange().contains(x) && map.getYRange().contains(y);
                if ((dx != 0 || dy != 0) && onMap) {
                    neighbours.add(map.at(x, y));
                }
            }
        }
        return neighbours;
    }

    private void wallOffAllButOne(Location centre, Location open) {
        for (Location tile : neighboursOf(centre)) {
            if (tile != open) {
                tile.setGround(new Wall());
            }
        }
    }

    private boolean isNextTo(Location a, Location b) {
        return Math.abs(a.x() - b.x()) <= 1 && Math.abs(a.y() - b.y()) <= 1;
    }

    /** A ground no actor can enter. */
    private static class Wall extends Ground {

        Wall() {
            super('#', "Wall");
        }

        @Override
        public boolean canActorEnter(Actor actor) {
            return false;
        }
    }

    /** A {@link RandomUtils} whose every random pick takes the option at one chosen index. */
    private static class PickIndex extends RandomUtils {

        private final int index;

        PickIndex(int index) {
            this.index = index;
        }

        @Override
        protected int doRandomInt(int bound) {
            return index;
        }
    }
}
