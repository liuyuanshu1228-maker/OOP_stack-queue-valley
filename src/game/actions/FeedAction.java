package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Feedable;
import game.items.food.Food;
import game.statistics.StatisticUtils;

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
        if (!StatisticUtils.trySpend(actor, ActorStatistics.STAMINA, STAMINA_COST)) {
            return actor + " is too tired to feed this animal.";
        }

        actor.getInventory().remove(food);

        String feedResult = feedAnimal(actor, map);
        applyFoodBonus(actor);
        return feedResult;
    }

    /**
     * Applies the animal's Feedable effect, if it has one.
     *
     * @param actor the actor doing the feeding
     * @param map the map the actor is on
     * @return a description of the animal's reaction, or an empty string if it has none
     */
    private String feedAnimal(Actor actor, GameMap map) {
        return animal.asCapability(Feedable.class)
                .map(feedable -> feedable.feed(actor, map.locationOf(actor)))
                .orElse("");
    }

    /**
     * Applies the food's bonus effect to the actor, if it has one.
     *
     * @param actor the actor who did the feeding
     */
    private void applyFoodBonus(Actor actor) {
        food.asCapability(Food.class).ifPresent(foodCapability -> foodCapability.applyFeedingBonus(actor));
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
