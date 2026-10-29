package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.creaturestate.ItemCache;
import game.positions.area.TileCondition;

import java.util.Optional;

/**
 * Met by the tiles that hold an Item worth foraging: one that the creature has
 * not already hidden away itself.
 * <p>
 * It also answers which Item that is, so the rule for "worth foraging" is
 * written once for the crow's senses and for its foraging.
 */
public class ForageableItems implements TileCondition {

    /** The creature that forages. */
    private final Actor creature;

    /**
     * Creates the condition for a creature.
     *
     * @param creature the creature that forages
     */
    public ForageableItems(Actor creature) {
        this.creature = creature;
    }

    /**
     * @param tile the tile to test
     * @return true if at least one forageable Item lies on the tile
     */
    @Override
    public boolean isMetBy(Location tile) {
        return firstAt(tile).isPresent();
    }

    /**
     * Finds the first Item on a tile that is worth foraging.
     *
     * @param tile the tile to look at
     * @return the first forageable Item, or empty if there is none
     */
    public Optional<Item> firstAt(Location tile) {
        for (Item item : tile.getItems()) {
            if (isForageable(item)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    /**
     * Whether the given Item is worth foraging: the creature has not cached it.
     *
     * @param item the Item to check
     * @return true if the creature has not cached this Item
     */
    private boolean isForageable(Item item) {
        Optional<ItemCache> cache = creature.asCapability(ItemCache.class);
        return cache.isEmpty() || !cache.get().remembers(item);
    }
}
