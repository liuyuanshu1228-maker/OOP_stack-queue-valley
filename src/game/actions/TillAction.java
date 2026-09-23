package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.StatisticUtils;
import game.ground.Tillable;
import game.items.TillingTool;

/**
 * An Action that allows an Actor to till the Ground at their current Location,
 * provided they are carrying a Hoe and have enough stamina.
 * The Ground must implement Tillable to define what it becomes once tilled.
 */
public class TillAction extends Action {

    /** Stamina points consumed by a successful till action. */
    private static final int STAMINA_COST = 2;

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
     * and has enough stamina, tills the Ground and deducts stamina; otherwise,
     * fails without any side effects.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Tillable tillable = location.getGroundAs(Tillable.class);

        boolean hasHoe = !actor.getInventory().getItemsAs(TillingTool.class).isEmpty();
        boolean hasEnoughStamina = StatisticUtils.hasEnough(actor, ActorStatistics.STAMINA, STAMINA_COST);

        if (tillable == null || !hasHoe || !hasEnoughStamina) {
            return actor + " is either too tired to till dirt or don't have a Hoe to till dirt.";
        }

        location.setGround(tillable.getTillResult());
        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, STAMINA_COST);
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
