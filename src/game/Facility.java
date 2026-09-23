package game;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ProcessAction;
import game.ground.ProcessingFacility;
import game.items.Processable;

/**
 * Abstract base class for processing facilities. While the Farmer is adjacent
 * to (or standing on) a Facility, a ProcessAction is offered for each
 * Processable Item the Farmer is carrying.
 */
public abstract class Facility extends Ground implements ProcessingFacility {

    /**
     * Constructor.
     *
     * @param displayChar character to display for this facility
     * @param name        the name of this facility
     */
    public Facility(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * Returns the list of Actions this facility offers the Actor, namely one
     * ProcessAction for every Processable Item the Actor is carrying.
     *
     * @param actor     the Actor who might process an Item at this facility
     * @param location  the current Location
     * @param direction the direction of this facility from the Actor
     * @return an ActionList containing a ProcessAction for each Processable Item carried
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        for (Item item : actor.getInventory().getItems()) {
            if (item.asCapability(Processable.class).isPresent()) {
                actions.add(new ProcessAction(this, item));
            }
        }
        return actions;
    }
}
