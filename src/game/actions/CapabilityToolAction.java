package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.tools.HasStaminaCost;
import game.statistics.StatisticAdjuster;

import java.util.List;

/**
 * Abstract base class for an Action that requires an Actor to be carrying a
 * specific kind of tool and to use it on a Ground that exposes a matching
 * capability, at a stamina cost determined by the tool by default (see
 * {@link #staminaCost(HasStaminaCost, Object)}).
 * <p>
 * Subclasses need only say which capability and tool types are involved, the
 * failure messages, and what the tool's effect actually does.
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
     * Constructor.
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
     * Checks that the Actor carries the required tool and that the Ground
     * exposes the required capability, deducts the stamina cost, and
     * delegates to the subclass-specific effect.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public final String execute(Actor actor, GameMap map) {
        List<T> tools = actor.getInventory().getItemsAs(toolType);
        if (tools.isEmpty()) {
            return actor + " " + noToolMessage;
        }

        C capability = location.getGroundAs(capabilityType);
        if (capability == null) {
            return actor + " " + noCapabilityMessage;
        }

        int cost = staminaCost(tools.get(0), capability);
        if (!new StatisticAdjuster(actor).trySpend(ActorStatistics.STAMINA, cost)) {
            return actor + " is too tired to do this.";
        }

        return applyEffect(actor, location, capability);
    }

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
     * checks have passed and stamina has already been deducted.
     *
     * @param actor the Actor performing the action
     * @param location the Location being acted on
     * @param capability the Ground's capability being acted on
     * @return a description of the outcome, to be displayed to the player
     */
    protected abstract String applyEffect(Actor actor, Location location, C capability);
}