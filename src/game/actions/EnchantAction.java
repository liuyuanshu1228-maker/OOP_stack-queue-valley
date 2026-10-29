package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.enchantments.Enchantment;
import game.items.tools.Enchantable;

/**
 * An Action that attaches one specific Enchantment to one specific
 * Enchantable tool the Actor is carrying. Which tool and which Enchantment
 * this offers is decided entirely by whoever constructs it (see Enchantable
 * tools' {@code allowableActions}, which offer one EnchantAction per known
 * Enchantment) -- this class does not know, or need to change for, how many
 * kinds of Enchantment exist.
 */
public class EnchantAction extends Action {

    /** The tool this Action attaches the Enchantment to. */
    private final Enchantable tool;

    /** The Enchantment this Action attaches. */
    private final Enchantment enchantment;

    /**
     * Creates a new EnchantAction.
     *
     * @param tool the Enchantable tool to enchant
     * @param enchantment the Enchantment to attach to it
     */
    public EnchantAction(Enchantable tool, Enchantment enchantment) {
        this.tool = tool;
        this.enchantment = enchantment;
    }

    /**
     * Attaches the Enchantment to the tool.
     *
     * @param actor the actor performing the action
     * @param map the map the actor is on
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        tool.enchant(enchantment);
        return actor + " enchants the " + tool + " with a " + enchantment + ".";
    }

    /**
     * Describes what this Action will do if chosen in the menu.
     *
     * @param actor the actor performing the action
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " enchants the " + tool + " with a " + enchantment;
    }
}
