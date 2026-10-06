package game.items.tools;

/**
 * Capability interface for a tool that has a stamina cost to use.
 */
public interface HasStaminaCost {

    /**
     * The stamina cost of using this tool.
     *
     * @return the stamina cost
     */
    int getStaminaCost();
}