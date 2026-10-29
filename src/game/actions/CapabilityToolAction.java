package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.tools.EnchantmentTrigger;
import game.items.tools.HasStaminaCost;
import game.statistics.StatisticAdjuster;

import java.util.List;
import java.util.Optional;

/**
 * Abstract base class for an Action that requires an Actor to be carrying a
 * specific kind of tool able to act on a Ground that exposes a matching
 * capability (see {@link #canUseOn}), at a stamina cost determined by the
 * tool by default (see {@link #staminaCost(HasStaminaCost, Object)}).
 * <p>
 * Subclasses need only say which capability and tool types are involved, the
 * failure messages, which of the Actor's tools can be used on which
 * capability, and what the tool's effect actually does. Once the effect has
 * been applied, this class also gives the tool a chance to fire any
 * Enchantment attached to it (see {@link EnchantmentTrigger}), so a subclass
 * never needs to know whether the tool it used happens to be enchantable.
 * Every outcome this class reports also names the Location it happened at,
 * so a subclass's own effect message only needs to say what happened, not
 * where.
 *
 * @param <C> the capability type required of the Ground (e.g. Tillable)
 * @param <T> the tool type required in the Actor's inventory
 */
public abstract class CapabilityToolAction<C, T extends HasStaminaCost> extends Action {

    /** The map location the Action acts on. */
    private final Location location;

    /** The capability type the Ground at location must expose. */
    private final Class<C> capabilityType;

    /** The tool type the Actor must be carrying. */
    private final Class<T> toolType;

    /** Message appended to the Actor when they lack the required tool. */
    private final String noToolMessage;

    /** Message appended to the Actor when the Ground lacks the required capability. */
    private final String noCapabilityMessage;

    /**
     * Creates a new CapabilityToolAction.
     *
     * @param location the Location to act on
     * @param capabilityType the capability type required of the Ground
     * @param toolType the tool type required in the Actor's inventory
     * @param noToolMessage message used when the Actor has no matching tool
     * @param noCapabilityMessage message used when the Ground has no matching capability
     */
    protected CapabilityToolAction(Location location, Class<C> capabilityType, Class<T> toolType,
                                   String noToolMessage, String noCapabilityMessage) {
        this.location = location;
        this.capabilityType = capabilityType;
        this.toolType = toolType;
        this.noToolMessage = noToolMessage;
        this.noCapabilityMessage = noCapabilityMessage;
    }

    /**
     * Checks that the Actor carries a tool able to act on the Ground's
     * capability, deducts the stamina cost, delegates to the
     * subclass-specific effect, then gives the tool used a chance to fire
     * any Enchantment attached to it. The Location is prepended to every
     * reported outcome, so subclasses never need to repeat it themselves.
     *
     * @param actor the actor performing the action
     * @param map the map the actor is on
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public final String execute(Actor actor, GameMap map) {
        List<T> tools = actor.getInventory().getItemsAs(toolType);
        if (tools.isEmpty()) {
            return at(actor + " " + noToolMessage);
        }

        C capability = location.getGroundAs(capabilityType);
        if (capability == null) {
            return at(actor + " " + noCapabilityMessage);
        }

        Optional<T> usableTool = findUsableTool(tools, capability);
        if (usableTool.isEmpty()) {
            return at(actor + " " + noToolMessage);
        }
        T tool = usableTool.get();

        int cost = staminaCost(tool, capability);
        if (!new StatisticAdjuster(actor).trySpend(ActorStatistics.STAMINA, cost)) {
            return at(actor + " is too tired to do this.");
        }

        String effectReport = at(applyEffect(actor, location, capability));
        return effectReport + new EnchantmentTrigger().fire(actor, tool, location);
    }

    /**
     * Prepends this Action's Location to an outcome message, so every
     * outcome -- success or failure -- names where it happened.
     *
     * @param message the outcome message to locate
     * @return the message prefixed with this Action's Location
     */
    private String at(String message) {
        return "At " + location + ": " + message;
    }

    /**
     * Finds, among the Actor's tools of the required type, one able to act
     * on the given capability.
     *
     * @param tools the Actor's tools of the required type
     * @param capability the Ground's capability being acted on
     * @return the first usable tool, or empty if none of them can act on it
     */
    private Optional<T> findUsableTool(List<T> tools, C capability) {
        for (T tool : tools) {
            if (canUseOn(tool, capability)) {
                return Optional.of(tool);
            }
        }
        return Optional.empty();
    }

    /**
     * Tells whether the given tool can be used on the given capability. Most
     * tools can act on every instance of the capability they offer (compare
     * {@link game.items.tools.BreakingTool#canBreak}, which lets a tool
     * refuse some targets) -- subclasses whose tool is always usable may
     * simply return true.
     *
     * @param tool the tool being considered
     * @param capability the Ground's capability being acted on
     * @return true if the tool can be used on this capability
     */
    protected abstract boolean canUseOn(T tool, C capability);

    /**
     * The stamina this Action costs. Defaults to the tool's own cost;
     * subclasses whose cost depends on what is being acted on override this.
     *
     * @param tool the tool being used
     * @param capability the Ground's capability being acted on
     * @return the stamina to deduct
     */
    protected int staminaCost(T tool, C capability) {
        return tool.getStaminaCost();
    }

    /**
     * Applies this Action's specific effect once the tool and capability
     * checks have passed and stamina has already been deducted. The caller
     * already names the Location this happened at, so this description only
     * needs to say what happened.
     *
     * @param actor the Actor performing the action
     * @param location the Location being acted on
     * @param capability the Ground's capability being acted on
     * @return a description of what happened, to be displayed to the player
     */
    protected abstract String applyEffect(Actor actor, Location location, C capability);
}
