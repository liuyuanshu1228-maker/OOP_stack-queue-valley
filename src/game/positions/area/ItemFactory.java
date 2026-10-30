package game.positions.area;

import edu.monash.fit2099.engine.items.Item;

/**
 * Produces a fresh Item each time it is asked. Lets an {@link ItemDropper}
 * place an independent Item instance on every tile of its area, rather than
 * the same shared instance on all of them.
 */
public interface ItemFactory {

    /**
     * @return a newly constructed Item
     */
    Item create();
}
