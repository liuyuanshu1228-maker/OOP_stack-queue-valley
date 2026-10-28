package game.pests;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.StackQueueValleyWorld;
import game.crops.Strawberry;
import game.crops.StrawberrySprout;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the turn every pest shares: feed when on a plant, otherwise move.
 */
class PestTurnTest {

    private GameMap map;
    private final Display display = new Display();
    private final SlimeSnail snail = new SlimeSnail();

    @BeforeEach
    void setUp() {
        FixedRandom.install();
        map = TestFarm.map(".....", ".....", ".....");
        new StackQueueValleyWorld(display).addGameMap(map);
    }

    @AfterEach
    void tearDown() {
        FixedRandom.uninstall();
    }

    @Test
    void feedsWhenStandingOnAPlant() throws Exception {
        map.at(1, 1).setGround(new Strawberry());
        map.at(1, 1).addActor(snail);

        Action action = snail.playTurn(new ActionList(), null, map, display);
        String report = action.execute(snail, map);

        assertInstanceOf(InfestAction.class, action);
        assertInstanceOf(StrawberrySprout.class, map.at(1, 1).getGround());
        assertTrue(report.contains("Slime Snail"), report);
    }

    @Test
    void movesTowardsAPlantWhenNotOnOne() throws Exception {
        map.at(1, 1).addActor(snail);
        map.at(4, 1).setGround(new Strawberry());

        Action action = snail.playTurn(new ActionList(), null, map, display);
        action.execute(snail, map);

        assertInstanceOf(MoveActorAction.class, action);
        assertEquals(2, map.locationOf(snail).x(), "one column closer to the plant");
    }

    @Test
    void aSwarmFeedsOnAPlantNextToItWithoutMoving() throws Exception {
        LocustSwarm swarm = new LocustSwarm();
        map.at(1, 1).addActor(swarm);
        map.at(2, 1).setGround(new Strawberry());

        Action action = swarm.playTurn(new ActionList(), null, map, display);
        action.execute(swarm, map);

        assertInstanceOf(InfestAction.class, action);
        assertEquals(1, map.locationOf(swarm).x());
        assertInstanceOf(StrawberrySprout.class, map.at(2, 1).getGround());
    }

    @Test
    void aMoleCanReachAPlantOneTileAway() throws Exception {
        BurrowingMole mole = new BurrowingMole();
        map.at(1, 1).addActor(mole);
        map.at(2, 1).setGround(new Strawberry());

        takeTurn(mole);

        assertEquals(2, map.locationOf(mole).x(), "lands on the plant, not past it");
    }

    @Test
    void aSwarmJumpsLikeAKnight() throws Exception {
        LocustSwarm swarm = new LocustSwarm();
        map.at(0, 0).addActor(swarm);
        map.at(4, 2).setGround(new Strawberry());

        takeTurn(swarm);

        assertEquals(2, map.locationOf(swarm).x());
        assertEquals(1, map.locationOf(swarm).y());
    }

    /**
     * Lets a pest choose and carry out one turn.
     *
     * @param pest the pest whose turn it is
     */
    private void takeTurn(Pest pest) {
        Action action = pest.playTurn(new ActionList(), null, map, display);
        action.execute(pest, map);
    }
}
