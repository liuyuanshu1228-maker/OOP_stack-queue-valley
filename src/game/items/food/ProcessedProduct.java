package game.items.food;

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
     * @return the preceding product's message followed by this layer's own
     */
    @Override
    public final String consume(Actor actor) {
        String precedingMessage = precedingProduct.asCapability(Consumable.class)
                .map(consumable -> consumable.consume(actor))
                .orElse("");
        String ownMessage = applyProcessingEffect(actor);
        return precedingMessage.isEmpty() ? ownMessage : precedingMessage + " " + ownMessage;
    }

    /**
     * Applies this specific processing layer's own consumption effect
     * (e.g. restoring HP, restoring stamina, changing a maximum statistic,
     * or placing an item on the ground).
     *
     * @param actor the Actor consuming this product
     * @return a description of this layer's actual effect
     */
    protected abstract String applyProcessingEffect(Actor actor);
}
