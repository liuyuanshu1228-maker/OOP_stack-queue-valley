package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Milk;
import game.positions.area.AreaFactory;
import game.positions.area.ItemDropper;
import game.random.RandomUtils;

/**
 * A Cow that wanders the farm and can be fed, producing Milk.
 */
public class Cow extends Animal {

    /** The Cow's starting (and maximum) hit points. */
    private static final int MAX_HP = 20;

    /** HP restored to the Cow when it is fed. */
    private static final int HEAL_AMOUNT = 5;

    /** The randomness handed to the Milk this Cow produces. */
    private final RandomUtils randomUtils;

    /** Builds the Area the Milk is dropped onto. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new Cow.
     *
     * @param name the name of the Cow
     * @param randomUtils the randomness handed to the Milk this Cow produces
     * @param areaFactory builds the Area the Milk is dropped onto
     */
    public Cow(String name, RandomUtils randomUtils, AreaFactory areaFactory) {
        super(name, 'C', MAX_HP, randomUtils);
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
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

        new ItemDropper(areaFactory.adjacentTo(farmerLocation)).dropOnRandomTile(new Milk(randomUtils));

        return this + " moos contentedly.";
    }
}
