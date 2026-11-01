package game.crops;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import game.items.food.FoodItem;
import game.statistics.StatisticAdjuster;

/**
 * A raw Tomato harvested from a {@link Tomato} crop.
 * Consuming it increases the Actor's maximum HP by 2, fully restoring current HP.
 * Using it to feed an animal also increases the Actor's maximum HP by 2, without
 * restoring current HP.
 */
public class TomatoItem extends FoodItem {

    /** Amount by which maximum HP increases, whether consumed or used to feed an animal. */
    private static final int MAX_HP_INCREASE = 2;

    /**
     * Creates a new TomatoItem.
     */
    public TomatoItem() {
        super("Tomato", 'o');
    }

    /**
     * Applies the effect of consuming this Tomato: increases the Actor's maximum HP,
     * which also fully restores the Actor's current HP to the new maximum.
     *
     * @param actor the Actor consuming this Tomato
     * @return a description of the actual maximum HP change and the
     * resulting current HP
     */
    @Override
    public String consume(Actor actor) {
        return eatToRaiseMaxHealth(actor, MAX_HP_INCREASE);
    }

    /**
     * Applies the bonus effect of using this Tomato to feed an animal: increases
     * the Farmer's maximum HP by 2, without restoring the current HP value
     * (unlike consuming a Tomato directly).
     *
     * @param farmer the Actor who fed an animal using this Tomato
     * @return a description of the bonus
     */
    @Override
    public String applyFeedingBonus(Actor farmer) {
        new StatisticAdjuster(farmer).increaseMaximumWithoutRestoring(
                ActorStatistics.HEALTH, MAX_HP_INCREASE);
        return farmer + "'s maximum HP rises by " + MAX_HP_INCREASE + " for feeding with a Tomato.";
    }
}
