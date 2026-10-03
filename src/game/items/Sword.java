package game.items;

import edu.monash.fit2099.engine.items.Item;

/**
 * A Sword bought from the Merchant. Used to fight hostile creatures in
 * the mines — see REQ4 for its combat behaviour.
 * 
 * Uses '!' to avoid clashing with the Strawberry crop/item's 'S'/'s'.
 */
public class Sword extends Item {

    /**
     * Constructor for Sword. Initializes the portable Sword item with display character '!'.
     */
    public Sword() {
        super("Sword", '!');
        this.makePortable();
    }
}