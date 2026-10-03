package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.items.tools.TillingTool;
import game.positions.Tillable;
import game.statistics.StatisticUtils;

import java.util.List;

/**
 * An Action that allows an Actor to till the Ground at their current Location,
 * provided they are carrying a Hoe and have enough stamina.
 * The Ground must implement Tillable to define what it becomes once tilled.
 */
public class TillAction extends Action {

    /** The map location whose ground is to be tilled. */
    private final Location location;

    /**
     * Constructor.
     *
     * @param location the Location whose Ground is to be tilled
     */
    public TillAction(Location location) {
        this.location = location;
    }

    /**
     * Performs the tilling attempt. If the Actor is carrying a tilling tool
     * and has enough stamina for that tool's cost, tills the Ground and
     * deducts stamina; otherwise, fails without any side effects.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Tillable tillable = location.getGroundAs(Tillable.class);
        List<TillingTool> tools = actor.getInventory().getItemsAs(TillingTool.class);

        if (tools.isEmpty()) {
            return actor + " has no hoe to till the ground.";
        }
        if (tillable == null) {
            return actor + " cannot till this ground.";
        }

        int staminaCost = tools.get(0).getStaminaCost();
        if (!StatisticUtils.hasEnough(actor, ActorStatistics.STAMINA, staminaCost)) {
            return actor + " is too tired to till the ground.";
        }

        location.setGround(tillable.getTillResult());
        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, staminaCost);
        return actor + " tills the ground.";
    }

    /**
     * Describes what this Action will do if chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " tills the ground here.";
    }
}
