package game.pestcontrol;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.StackQueueValleyWorld;
import game.crops.Strawberry;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that a deterrent item is offered, used and used up.
 */
class DeployDeterrentActionTest {

    private GameMap map;
    private final Holder farmer = new Holder();

    @BeforeEach
    void setUp() throws Exception {
        map = TestFarm.map(",,,", ",,,", ",,,");
        new StackQueueValleyWorld(new Display()).addGameMap(map);
        map.at(1, 1).addActor(farmer);
    }

    @Test
    void usingAnItemUsesItUp() {
        SaltBarrier salt = new SaltBarrier();
        farmer.getInventory().add(salt);

        ActionList actions = salt.allowableActions(farmer, map);
        String report = actions.get(0).execute(farmer, map);

        assertEquals(1, actions.size());
        assertTrue(farmer.getInventory().getItems().isEmpty());
        assertTrue(report.contains("Salt covers"), report);
    }

    @Test
    void aTrapIsNotOfferedOnAPlant() {
        PestTrap trap = new PestTrap();
        map.at(1, 1).setGround(new Strawberry());

        assertEquals(0, trap.allowableActions(farmer, map).size());
    }

    @Test
    void aTrapIsSetWhereTheFarmerStands() {
        PestTrap trap = new PestTrap();
        farmer.getInventory().add(trap);

        trap.allowableActions(farmer, map).get(0).execute(farmer, map);

        assertInstanceOf(TrapGround.class, map.at(1, 1).getGround());
    }

    /**
     * A plain actor that carries the items.
     */
    private static class Holder extends Actor {

        Holder() {
            super("Farmer", '@', 10);
        }

        @Override
        public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
            return new DoNothingAction();
        }
    }
}
