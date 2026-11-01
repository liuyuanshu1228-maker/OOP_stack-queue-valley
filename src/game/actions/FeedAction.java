package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.statistics.StatisticAdjuster;

import java.util.Optional;

/**
 * An Action that allows the Farmer to feed an adjacent animal using a specific food Item.
 * Requires at least 2 stamina; consumes the food item and applies both the animal's
 * feeding effect and the food's bonus effect on success.
 */
public class FeedAction extends Action {

    /** Stamina points consumed by a successful feed action. */
    private static final int STAMINA_COST = 2;

    /** The food item consumed during the feed action. */
    private final Item food;

    /** The animal actor being fed. */
    private final Actor animal;

    /**
     * Creates a new FeedAction.
     *
     * @param food the food Item to be used for feeding
     * @param animal the Actor (animal) to be fed
     */
    public FeedAction(Item food, Actor animal) {
        this.food = food;
        this.animal = animal;
    }

    /**
     * Performs the feeding attempt. The animal is checked first: if it cannot be
     * fed, or the Actor does not have enough stamina, nothing is spent. Otherwise
     * deducts the stamina, removes the food from the inventory, and applies both
     * the animal's Feedable effect and the food's Food bonus effect.
     *
     * @param actor the actor performing the action
     * @param map the map the actor is on
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Optional<Feedable> feedable = animal.asCapability(Feedable.class);
        if (feedable.isEmpty()) {
            return actor + " cannot feed " + animal + ".";
        }
        if (!new StatisticAdjuster(actor).trySpend(ActorStatistics.STAMINA, STAMINA_COST)) {
            return actor + " is too tired to feed this animal.";
        }

        actor.getInventory().remove(food);

        String feedResult = feedable.get().feed(actor, map.locationOf(actor));
        return feedResult + applyFoodBonus(actor);
    }

    /**
     * Applies the food's bonus effect to the actor, if it has one.
     *
     * @param actor the actor who did the feeding
     * @return " " followed by a description of the bonus, or an empty string if the food has none
     */
    private String applyFoodBonus(Actor actor) {
        Optional<Food> foodCapability = food.asCapability(Food.class);
        if (foodCapability.isEmpty()) {
            return "";
        }
        return " " + foodCapability.get().applyFeedingBonus(actor);
    }

    /**
     * Describes what this Action will do if chosen in the menu.
     *
     * @param actor the actor performing the action
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " feeds " + animal + " with " + food;
    }
}
