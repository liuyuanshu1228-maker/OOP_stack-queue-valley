package edu.monash.fit2099.engine.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class that manages a collection of items, allowing for clean abstraction
 * and the implementation of inventory mechanics like stacking.
 *
 * @author Adrian Kristanto
 */
public class Inventory {
    /**
     * A bag of items
     */
    private final List<Item> items = new ArrayList<>();

    /**
     * Add an item to the inventory.
     *
     * @param item The Item to add.
     */
    public void add(Item item) {
        items.add(item);
    }

    /**
     * Remove an item from the inventory.
     *
     * @param item The Item to remove.
     */
    public void remove(Item item) {
        items.remove(item);
    }

    /**
     * Get a copy of the inventory.
     *
     * @return An unmodifiable wrapper of the inventory.
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Returns a list of all capabilities of the specified type that are present
     * in the item inventory. For each item, attempts to retrieve the capability
     * using {@code asCapability}. If the capability is present, it is included
     * in the returned list.
     *
     * @param <T> the type of capability to retrieve
     * @param capability the class object representing the capability type
     * @return a list containing all capabilities of the specified type found in the inventory;
     *         the list is empty if no items provide the capability
     */
    public <T> List<T> getItemsAs(Class<T> capability) {
        List<T> result = new ArrayList<>();
        for (Item item : items) {
            if(item != null){
                item.asCapability(capability).ifPresent(result::add);
            }
        }
        return result;
    }

}
