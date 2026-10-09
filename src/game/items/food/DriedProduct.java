package game.items.food;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import game.statistics.StatisticAdjuster;

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
     * Creates a new DriedProduct.
     *
     * @param precedingProduct the complete product this layer wraps
     */
    public DriedProduct(Item precedingProduct) {
        super(MODIFIER, precedingProduct);
    }

    /**
     * Drying restores stamina, so only an Actor that has stamina can receive it.
     *
     * @param actor the Actor that would receive the effect
     * @return true if the Actor has stamina
     */
    @Override
    protected boolean canReceiveProcessingEffect(Actor actor) {
        return actor.hasStatistic(ActorStatistics.STAMINA);
    }

    /**
     * Applies this layer's own effect: restores 2 stamina to the Actor.
     *
     * @param actor the Actor consuming this product
     * @return a description of the actual stamina change
     */
    @Override
    protected String applyProcessingEffect(Actor actor) {
        return "The drying restores "
                + new StatisticAdjuster(actor).increase(
                        ActorStatistics.STAMINA, STAMINA_RESTORED).describe("stamina") + ".";
    }
}
