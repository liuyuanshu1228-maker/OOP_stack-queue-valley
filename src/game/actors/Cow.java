package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Milk;
import game.positions.area.ItemDropper;

/**
 * A Cow that wanders the farm and can be fed, producing Milk.
 */
public class Cow extends Animal {

    /** The Cow's starting (and maximum) hit points. */
    private static final int MAX_HP = 20;

    /** HP restored to the Cow when it is fed. */
    private static final int HEAL_AMOUNT = 5;

    /**
     * Creates a new Cow.
     *
     * @param name the name of the Cow
     */
    public Cow(String name) {
        super(name, 'C', MAX_HP);
    }

    /**
     * Applies the effect of feeding this Cow: restores 5 HP (without exceeding
     * its maximum HP) and drops one Milk on a tile adjacent to the Farmer.
     *
     * @param farmer the Actor doing the feeding
     * @param farmerLocation the Location of the Farmer
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String feed(Actor farmer, Location farmerLocation) {
        this.heal(HEAL_AMOUNT);

        ItemDropper.adjacentTo(farmerLocation).dropOnRandomTile(new Milk());

        return this + " moos contentedly.";
    }
}
