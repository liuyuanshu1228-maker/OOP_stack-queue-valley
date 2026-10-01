package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.positions.Tomato;
import game.statistics.StatisticUtils;

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
     * Constructor.
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
        int maxHealthBefore = actor.getMaximumStatistic(ActorStatistics.HEALTH);
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, MAX_HP_INCREASE);
        int maxHealthAfter = actor.getMaximumStatistic(ActorStatistics.HEALTH);
        int healthAfter = actor.getStatistic(ActorStatistics.HEALTH);

        return actor + " eats the tomato. Maximum HP increases by " + (maxHealthAfter - maxHealthBefore)
                + " (" + maxHealthBefore + " -> " + maxHealthAfter + "), and current HP is restored to " + healthAfter + ".";
    }

    /**
     * Applies the bonus effect of using this Tomato to feed an animal: increases
     * the Farmer's maximum HP by 2, without restoring the current HP value
     * (unlike consuming a Tomato directly).
     *
     * @param farmer the Actor who fed an animal using this Tomato
     */
    @Override
    public void applyFeedingBonus(Actor farmer) {
        StatisticUtils.increaseMaximumWithoutRestoring(farmer, ActorStatistics.HEALTH, MAX_HP_INCREASE);
    }
}
