package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import game.items.food.FoodItem;
import game.statistics.StatisticAdjuster;

/**
 * A raw Parsnip harvested from a {@link Parsnip} crop.
 * Consuming it restores 1 HP, without exceeding the Actor's maximum HP.
 * Using it to feed an animal increases the Actor's maximum stamina by 1 point.
 */
public class ParsnipItem extends FoodItem {

    /** HP restored when this Parsnip is consumed. */
    private static final int HP_RESTORED = 1;

    /** Amount by which maximum stamina increases when this Parsnip is used to feed an animal. */
    private static final int MAX_STAMINA_INCREASE = 1;

    /**
     * Constructor.
     */
    public ParsnipItem() {
        super("Parsnip", 'p');
    }

    /**
     * Applies the effect of consuming this Parsnip: restores 1 HP to the Actor,
     * without exceeding the Actor's maximum HP.
     *
     * @param actor the Actor consuming this Parsnip
     * @return a description of the actual HP change, accounting for the
     * actor's HP already being at or near its maximum
     */
    @Override
    public String consume(Actor actor) {
        return eatToHeal(actor, HP_RESTORED);
    }

    /**
     * Applies the bonus effect of using this Parsnip to feed an animal: increases
     * the Farmer's maximum stamina by 1 point, without restoring the current
     * stamina value.
     *
     * @param farmer the Actor who fed an animal using this Parsnip
     */
    @Override
    public void applyFeedingBonus(Actor farmer) {
        new StatisticAdjuster(farmer).increaseMaximumWithoutRestoring(
                ActorStatistics.STAMINA, MAX_STAMINA_INCREASE);
    }
}
