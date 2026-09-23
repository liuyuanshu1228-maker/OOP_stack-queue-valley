package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;

/**
 * Abstract base class for a single layer of processing applied to a product.
 * Each ProcessedProduct wraps exactly one preceding product (which may itself
 * be raw or already processed), without mutating it. Consuming a ProcessedProduct
 * delegates to the preceding product's Consumable effect exactly once, then applies
 * this layer's own effect, so that effects are applied in the order the layers were added.
 */
public abstract class ProcessedProduct extends ConsumableItem implements Processable {

    /** The complete product immediately before this processing layer was applied. */
    private final Item precedingProduct;

    /**
     * Constructor.
     *
     * @param nameModifier     the word prepended to the preceding product's name for this layer, e.g. "Preserved"
     * @param precedingProduct the complete product this layer wraps (raw or already processed)
     */
    public ProcessedProduct(String nameModifier, Item precedingProduct) {
        super(nameModifier + " " + precedingProduct, precedingProduct.getDisplayChar());
        this.precedingProduct = precedingProduct;
    }

    /**
     * Consumes this product: delegates to the preceding product's consume effect
     * exactly once, then applies this layer's own effect.
     *
     * @param actor the Actor consuming this product
     */
    @Override
    public final void consume(Actor actor) {
        precedingProduct.asCapability(Consumable.class).ifPresent(consumable -> consumable.consume(actor));
        applyProcessingEffect(actor);
    }

    /**
     * Applies this specific processing layer's own consumption effect
     * (e.g. restoring HP, restoring stamina, changing a maximum statistic,
     * or placing an item on the ground).
     *
     * @param actor the Actor consuming this product
     */
    protected abstract void applyProcessingEffect(Actor actor);
}
