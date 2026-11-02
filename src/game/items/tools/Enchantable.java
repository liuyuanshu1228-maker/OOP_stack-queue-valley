package game.items.tools;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Capability interface for Items that can carry an {@link Enchantment} and
 * trigger it the next time they are used for their ordinary purpose.
 * Implementing classes own their own storage (typically by composing an
 * {@link EnchantmentSlot}) and combine the Enchantment's effect with their
 * own, independent tool-specific effect.
 */
public interface Enchantable {

    /**
     * Attaches the given Enchantment to this tool, replacing any Enchantment
     * already attached.
     *
     * @param enchantment the Enchantment to attach
     */
    public void enchant(Enchantment enchantment);

    /**
     * While this tool is enchanted, applies this tool's own effect and fires
     * its attached Enchantment against the tiles that effect touched, then
     * consumes the Enchantment. Does nothing while unenchanted.
     *
     * @param actor the Actor using the tool
     * @param location the Location the tool was used at
     * @return a description of what fired, or "" while unenchanted; never null
     */
    public String triggerEnchantment(Actor actor, Location location);
}
