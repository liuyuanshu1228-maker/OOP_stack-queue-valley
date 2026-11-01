package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.StackQueueValleyWorld;
import game.crops.Strawberry;
import game.pests.SlimeSnail;
import game.positions.area.AreaFactory;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests where SeekPlantBehaviour lands a pest. Each test uses its own small
 * set of landing offsets so the expected tile is unambiguous.
 */
class SeekPlantBehaviourTest {

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);

    private static final int[][] LEFT_OR_RIGHT = {{1, 0}, {-1, 0}};

    private GameMap map;
    private Actor pest;
    private final Action fallbackAction = new DoNothingAction();
    private final Behaviour<Actor, Action> fallback = new FixedFallback(fallbackAction);

    @BeforeEach
    void setUp() {
        map = TestFarm.map(".......", ".......", ".......");
        new StackQueueValleyWorld(new Display()).addGameMap(map);
        pest = new SlimeSnail(random, areaFactory);
    }


    @Test
    void landsOnTheTileTowardsTheNearestPlant() throws Exception {
        map.at(1, 1).addActor(pest);
        map.at(4, 1).setGround(new Strawberry(random, areaFactory));

        landWith(LEFT_OR_RIGHT, map.at(1, 1));

        assertSame(map.at(2, 1), map.locationOf(pest));
    }

    @Test
    void landsOnARandomTileWhenNoPlantIsInRange() throws Exception {
        map.at(1, 1).addActor(pest);

        landWith(new int[][] {{-1, 0}, {1, 0}}, map.at(1, 1));

        assertSame(map.at(0, 1), map.locationOf(pest), "FixedRandom picks the first landing");
    }

    @Test
    void ignoresLandingTilesOffTheMap() throws Exception {
        map.at(0, 1).addActor(pest);

        landWith(new int[][] {{-1, 0}, {1, 0}}, map.at(0, 1));

        assertSame(map.at(1, 1), map.locationOf(pest));
    }

    @Test
    void jumpsOverATileItCannotEnter() throws Exception {
        map.at(0, 1).addActor(pest);
        map.at(1, 1).setGround(new Wall());

        landWith(new int[][] {{2, 0}}, map.at(0, 1));

        assertSame(map.at(2, 1), map.locationOf(pest));
    }

    @Test
    void usesTheFallbackWhenNoLandingTileCanBeEntered() {
        map.at(2, 1).setGround(new Wall());
        map.at(0, 1).setGround(new Wall());

        Action chosen = new SeekPlantBehaviour(LEFT_OR_RIGHT, fallback, areaFactory).operate(pest, map.at(1, 1));

        assertSame(fallbackAction, chosen);
    }

    @Test
    void laterChangesToTheOffsetsHaveNoEffect() throws Exception {
        map.at(1, 1).addActor(pest);
        int[][] offsets = {{1, 0}};
        SeekPlantBehaviour behaviour = new SeekPlantBehaviour(offsets, fallback, areaFactory);
        offsets[0][0] = -1;

        behaviour.operate(pest, map.at(1, 1)).execute(pest, map);

        assertSame(map.at(2, 1), map.locationOf(pest));
    }

    @Test
    void rejectsMissingOrMalformedOffsets() {
        assertRejected(new int[0][]);
        assertRejected(new int[][] {{1}});
    }

    private void assertRejected(int[][] offsets) {
        try {
            new SeekPlantBehaviour(offsets, fallback, areaFactory);
            fail("Expected the offsets to be rejected");
        } catch (IllegalArgumentException expected) {
            // The offsets were rejected, as they should be.
        }
    }

    private void landWith(int[][] offsets, Location from) {
        new SeekPlantBehaviour(offsets, fallback, areaFactory).operate(pest, from).execute(pest, map);
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

    /** A fallback behaviour that always returns the same action. */
    private static class FixedFallback implements Behaviour<Actor, Action> {

        private final Action action;

        FixedFallback(Action action) {
            this.action = action;
        }

        @Override
        public Action operate(Actor actor, Location location) {
            return action;
        }
    }
}
