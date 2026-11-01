package game.items.tools;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Composition helper used by the Actions that drive an {@link Enchantable}
 * tool (tilling/watering via {@code CapabilityToolAction}, breaking via
 * {@code BreakAction}): given the specific tool reference already obtained
 * from the Actor's inventory, finds that same Item's Enchantable view and
 * fires it. Written once here so the two Actions do not each repeat the
 * identical lookup.
 * <p>
 * {@code Inventory.getItemsAs} returns views already narrowed to one
 * capability type, and a narrowed reference cannot be asked for a second,
 * unrelated capability -- only {@code Item}/{@code GameEntity} expose
 * {@code asCapability}, which the narrowed reference's static type does not.
 * So the matching Enchantable view is found by reference equality against
 * the tool already in hand, rather than by casting or an instanceof check.
 */
public final class EnchantmentTrigger {

    /**
     * Creates a trigger that fires the Enchantment of a tool after the tool is used.
     */
    public EnchantmentTrigger() {
    }

    /**
     * Finds the Enchantable view of the given tool and fires it.
     *
     * @param actor the Actor who used the tool
     * @param usedTool the specific tool instance that was just used
     * @param location the Location the tool was used at
     * @return " " followed by a description of what fired, or "" if the tool
     * carries no Enchantable capability or is currently unenchanted
     */
    public String fire(Actor actor, Object usedTool, Location location) {
        for (Enchantable enchantable : actor.getInventory().getItemsAs(Enchantable.class)) {
            if (enchantable == usedTool) {
                String message = enchantable.triggerEnchantment(actor, location);
                return message.isEmpty() ? "" : " " + message;
            }
        }
        return "";
    }
}
