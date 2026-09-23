package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.StatisticUtils;
import game.actors.Feedable;
import game.items.Food;

/**
 * An Action that allows the Farmer to feed an adjacent animal using a specific food Item.
 * Requires at least 2 stamina; consumes the food item and applies both the animal's
 * feeding effect and the food's bonus effect on success.
 */
public class FeedAction extends Action {

    /** Stamina points consumed by a successful feed action. */
    private static final int STAMINA_COST = 2;

    private final Item food;
    private final Actor animal;

    /**
     * Constructor.
     *
     * @param food   the food Item to be used for feeding
     * @param animal the Actor (animal) to be fed
     */
    public FeedAction(Item food, Actor animal) {
        this.food = food;
        this.animal = animal;
    }

    /**
     * Performs the feeding attempt. If the Actor has enough stamina, deducts it,
     * removes the food from inventory, and applies both the animal's Feedable
     * effect and the food's Food bonus effect; otherwise, fails without any
     * side effects.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        boolean hasEnoughStamina = StatisticUtils.hasEnough(actor, ActorStatistics.STAMINA, STAMINA_COST);

        if (!hasEnoughStamina) {
            return actor + " is too tired to feed this animal.";
        }

        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, STAMINA_COST);
        actor.getInventory().remove(food);

        Feedable feedable = animal.asCapability(Feedable.class).orElse(null);
        Location farmerLocation = map.locationOf(actor);
        String feedResult = feedable != null ? feedable.feed(actor, farmerLocation) : "";

        Food foodCapability = food.asCapability(Food.class).orElse(null);
        if (foodCapability != null) {
            foodCapability.applyFeedingBonus(actor);
        }

        return feedResult;
    }

    /**
     * Describes what this Action will do if chosen in the menu.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " feeds " + animal + " with " + food;
    }
}
