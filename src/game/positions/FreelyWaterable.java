package game.positions;

/**
 * Capability interface for Ground that can be watered for free, without a
 * tool, stamina cost, or Actor involved — e.g. by a crow's droppings.
 * Implementing classes define what receiving a free watering does.
 */
public interface FreelyWaterable {

    /**
     * Applies the effect of being watered for free.
     */
    void receiveFreeWatering();
}