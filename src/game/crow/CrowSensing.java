package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ItemCache;

/**
 * Shared predicates used by this crow's states to query what is around it,
 * so each state's transition logic stays a short, readable expression
 * instead of repeating the same checks.
 */
final class CrowSensing {

    private CrowSensing() {
    }

    /**
     * Whether there is an Item at this Location worth foraging: one that
     * the given creature hasn't already hidden away itself.
     *
     * @param location the Location to check
     * @param creature the creature doing the sensing
     * @return true if there is at least one forageable Item at this Location
     */
    static boolean hasForageableItems(Location location, Actor creature) {
        ItemCache cache = creature.asCapability(ItemCache.class).orElse(null);

        for (Item item : location.getItems()) {
            if (cache == null || !cache.remembers(item)) {
                return true;
            }
        }

        return false;
    }
}
