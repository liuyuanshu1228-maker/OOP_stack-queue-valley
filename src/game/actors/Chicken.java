package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Egg;
import game.positions.LocationUtils;

import java.util.Random;

/**
 * A Chicken that wanders the farm and can be fed, producing Eggs.
 */
public class Chicken extends Animal {

    /** The Chicken's starting (and maximum) hit points. */
    private static final int MAX_HP = 10;

    /** HP restored to the Chicken when it is fed. */
    private static final int HEAL_AMOUNT = 3;

    /** Chance (0.0 to 1.0) that a second Egg is dropped. */
    private static final double BONUS_EGG_CHANCE = 0.5;

    private final Random random = new Random();

    /**
     * Constructor.
     * <p>
     * Uses 'n' rather than the more obvious 'c', which is already taken by
     * the Cauliflower item in this world.
     *
     * @param name the name of the Chicken
     */
    public Chicken(String name) {
        super(name, 'n', MAX_HP);
    }

    /**
     * Applies the effect of feeding this Chicken: restores 3 HP (without
     * exceeding its maximum HP) and drops one Egg on a tile adjacent to the
     * Farmer, with a 50% chance of a second Egg being dropped as well.
     *
     * @param farmer         the Actor doing the feeding
     * @param farmerLocation the Location of the Farmer
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String feed(Actor farmer, Location farmerLocation) {
        this.heal(HEAL_AMOUNT);

        LocationUtils.dropItemNearby(farmerLocation, new Egg(), random);
        if (random.nextDouble() < BONUS_EGG_CHANCE) {
            LocationUtils.dropItemNearby(farmerLocation, new Egg(), random);
        }

        return this + " clucks happily.";
    }
}
