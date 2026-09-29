package game.items;

/**
 * Capability interface for Items that can be used to till Ground.
 * Any Item implementing this interface can perform a TillAction.
 */
public interface TillingTool {

    /**
     * The stamina cost of using this tool to till something.
     *
     * @return the stamina cost of tilling with this tool
     */
    int getStaminaCost();
}
