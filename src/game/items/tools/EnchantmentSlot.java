package game.items.tools;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Composition helper that stores at most one {@link Enchantment}, fires it,
 * then clears itself. Used internally by every {@link Enchantable} tool so
 * the generic store/query/fire/consume behaviour is written once, instead of
 * being repeated in each tool alongside its own, unrelated tool-specific
 * effect (which would mix two unrelated reasons to change into one class).
 */
public final class EnchantmentSlot {

    /** The Enchantment currently held, or null if none is attached. */
    private Enchantment enchantment;

    /**
     * Creates an empty slot with no Enchantment attached.
     */
    public EnchantmentSlot() {
    }

    /**
     * Attaches the given Enchantment, replacing any Enchantment already held.
     *
     * @param enchantment the Enchantment to attach
     */
    public void enchant(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    /**
     * Tells whether this slot currently holds no Enchantment. The host tool
     * uses this to decide whether to perform its own enchanted-only effect
     * before calling {@link #fire}.
     *
     * @return true if no Enchantment is attached
     */
    public boolean isEmpty() {
        return enchantment == null;
    }

    /**
     * Fires the held Enchantment against the given tiles, then clears this slot.
     *
     * @param affectedTiles the tiles the host tool's own effect touched
     * @param activator the Actor who triggered the effect
     * @param origin the Location the host tool was used at
     * @return a description of what the Enchantment did, or "" if none was held
     */
    public String fire(List<Location> affectedTiles, Actor activator, Location origin) {
        if (enchantment == null) {
            return "";
        }
        String message = enchantment.applyTo(affectedTiles, activator, origin);
        enchantment = null;
        return message;
    }
}
