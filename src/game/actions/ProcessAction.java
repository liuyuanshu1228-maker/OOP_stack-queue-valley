package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.positions.ProcessingFacility;

/**
 * An Action that allows the Farmer to process a Processable Item they are carrying
 * using an adjacent ProcessingFacility, replacing it with one new processed layer.
 * Costs one turn and no stamina.
 */
public class ProcessAction extends Action {

    /** The processing facility (e.g. DryingRack, PreservesJar) performing the transformation. */
    private final ProcessingFacility facility;

    /** The item in the actor's inventory to be processed. */
    private final Item product;

    /**
     * Constructor.
     *
     * @param facility the ProcessingFacility to use
     * @param product  the Item to be processed
     */
    public ProcessAction(ProcessingFacility facility, Item product) {
        this.facility = facility;
        this.product = product;
    }

    /**
     * Replaces the selected product in the Actor's inventory with a new
     * processed layer produced by the facility.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Item processedProduct = facility.process(product);
        actor.getInventory().remove(product);
        actor.getInventory().add(processedProduct);
        return actor + " processes " + product + " into " + processedProduct + ".";
    }

    /**
     * Describes what this Action will do if chosen in the menu. Names the
     * facility as well as the product, so that two different facilities
     * offering to process the same item (e.g. a Drying Rack and a Preserves
     * Jar both adjacent to the Actor) are not shown as identical menu
     * entries.
     *
     * @param actor The actor performing the action.
     * @return the action description to be displayed on the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " processes " + product + " at the " + facility + ".";
    }
}
