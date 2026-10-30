package game.items.tools;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.List;

/**
 * Base class for an Item that can be enchanted. Owns the storage of the
 * Enchantments it may be enchanted with and of the one currently attached,
 * and offers one {@link EnchantAction} per available Enchantment -- the
 * behaviour every {@link Enchantable} tool needs regardless of what its own
 * tool-specific effect is. A concrete subclass adds only that effect, through
 * {@link Enchantable#triggerEnchantment}.
 * <p>
 * The list of Enchantments a tool can be enchanted with is given to it at
 * construction (see the composition root, {@code StackQueueValleyWorld}),
 * rather than being looked up by this class, so that which Enchantments exist
 * never has to be written into the tool itself.
 */
public abstract class AbstractEnchantableTool extends Item implements Enchantable {

    /** The Enchantments this tool can be enchanted with, injected at construction. */
    private final List<Enchantment> offeredEnchantments;

    /** Stores the Enchantment currently attached to this tool, if any. */
    private final EnchantmentSlot enchantmentSlot = new EnchantmentSlot();

    /**
     * Creates a new enchantable tool.
     *
     * @param name the tool's display name
     * @param displayChar the character used to display the tool
     * @param offeredEnchantments the Enchantments this tool can be enchanted with
     */
    protected AbstractEnchantableTool(String name, char displayChar, List<Enchantment> offeredEnchantments) {
        super(name, displayChar);
        this.offeredEnchantments = offeredEnchantments;
    }

    /**
     * Offers one EnchantAction for each Enchantment this tool can be enchanted with,
     * in addition to whatever actions the Item base class already offers its owner.
     *
     * @param owner the Actor carrying this tool
     * @param map the map the Actor is on
     * @return an ActionList including one EnchantAction per available Enchantment
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);
        for (Enchantment enchantment : offeredEnchantments) {
            actions.add(new EnchantAction(this, enchantment));
        }
        return actions;
    }

    /**
     * Attaches the given Enchantment to this tool, replacing any Enchantment
     * already attached.
     *
     * @param enchantment the Enchantment to attach
     */
    @Override
    public void enchant(Enchantment enchantment) {
        enchantmentSlot.enchant(enchantment);
    }

    /**
     * Exposes this tool's EnchantmentSlot to subclasses, so each one can check
     * whether it is currently enchanted and fire the attached Enchantment once
     * its own tool-specific effect has run.
     *
     * @return the slot that stores and fires this tool's attached Enchantment
     */
    protected EnchantmentSlot enchantmentSlot() {
        return enchantmentSlot;
    }
}
