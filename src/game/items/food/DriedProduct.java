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
        return "The drying restores "
                + new StatisticAdjuster(actor).increaseAndDescribe(
                        ActorStatistics.STAMINA, STAMINA_RESTORED, "stamina") + ".";
    }
}
