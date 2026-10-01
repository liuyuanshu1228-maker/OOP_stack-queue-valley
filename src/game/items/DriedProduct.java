package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * A single "Dried" processing layer, produced by a DryingRack.
 * Each Dried layer restores 2 stamina to the Actor when consumed,
 * on top of the preceding product's own effect.
 */
public class DriedProduct extends ProcessedProduct {

    /** Stamina restored by this layer when consumed. */
    private static final int STAMINA_RESTORED = 2;

    /** The name modifier prepended for this layer. */
    private static final String MODIFIER = "Dried";

    /**
     * Constructor.
     *
     * @param precedingProduct the complete product this layer wraps
     */
    public DriedProduct(Item precedingProduct) {
        super(MODIFIER, precedingProduct);
    }

    /**
     * Applies this layer's own effect: restores 2 stamina to the Actor.
     *
     * @param actor the Actor consuming this product
     * @return a description of the actual stamina change
     */
    @Override
    protected String applyProcessingEffect(Actor actor) {
        int staminaBefore = actor.getStatistic(ActorStatistics.STAMINA);
        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.INCREASE, STAMINA_RESTORED);
        int staminaAfter = actor.getStatistic(ActorStatistics.STAMINA);

        return "The drying restores " + (staminaAfter - staminaBefore) + " stamina ("
                + staminaBefore + " -> " + staminaAfter + ").";
    }
}
