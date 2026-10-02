package game.items.food;

/**
 * Marker capability interface for Items that can be processed by a ProcessingFacility.
 * Raw crops (e.g. Parsnip, Tomato) and all ProcessedProduct layers implement this,
 * allowing a facility to process either a raw product or an already-processed one.
 */
public interface Processable {
}
