package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * A single "Preserved" processing layer, produced by a PreservesJar.
 * Each Preserved layer restores 1 HP to the Actor when consumed,
 * on top of the preceding product's own effect.
 */
public class PreservedProduct extends ProcessedProduct {

    /** HP restored by this layer when consumed. */
    private static final int HP_RESTORED = 1;

    /** The name modifier prepended for this layer. */
    private static final String MODIFIER = "Preserved";

    /**
     * Constructor.
     *
     * @param precedingProduct the complete product this layer wraps
     */
    public PreservedProduct(Item precedingProduct) {
        super(MODIFIER, precedingProduct);
    }

    /**
     * Applies this layer's own effect: restores 1 HP to the Actor.
     *
     * @param actor the Actor consuming this product
     * @return a description of the actual HP change
     */
    @Override
    protected String applyProcessingEffect(Actor actor) {
        int healthBefore = actor.getStatistic(ActorStatistics.HEALTH);
        actor.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.INCREASE, HP_RESTORED);
        int healthAfter = actor.getStatistic(ActorStatistics.HEALTH);

        return "The preserving restores " + (healthAfter - healthBefore) + " HP ("
                + healthBefore + " -> " + healthAfter + ").";
    }
}
