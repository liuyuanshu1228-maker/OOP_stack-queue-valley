package game.plants;

import edu.monash.fit2099.engine.positions.Location;
import game.positions.Plantable;
import game.positions.area.AreaFactory;
import game.positions.area.PlantableTile;
import game.random.RandomUtils;

import java.util.Optional;

/**
 * A plant ability that plants a new plant on a random adjacent tile that can
 * be planted on (see {@link Plantable}).
 * <p>
 * The new plant is created by the plant itself, via {@link Spreading#offspring()},
 * and may be the same kind as this one or an earlier stage, e.g. a
 * {@code StrawberrySprout}'s {@code offspring()} returns a new
 * {@code StrawberrySeedling}. If the plant is not {@link Spreading}, or no
 * adjacent tile can be planted on, nothing happens.
 */
public class SelfReplicate extends PeriodicAbility {

    /** Builds the Area of tiles this ability searches for a spot on. */
    private final AreaFactory areaFactory;

    /**
     * Creates a SelfReplicate ability that always happens on its turn.
     *
     * @param intervalTurns how many turns of age between attempts to spread
     * @param randomUtils the randomness used to roll the chance
     * @param areaFactory builds the Area of tiles searched for a spot to spread onto
     */
    public SelfReplicate(int intervalTurns, RandomUtils randomUtils, AreaFactory areaFactory) {
        super(intervalTurns, randomUtils);
        this.areaFactory = areaFactory;
    }

    /**
     * Creates a SelfReplicate ability with the given chance.
     *
     * @param intervalTurns how many turns of age between attempts to spread
     * @param chancePercent the chance, as a percentage, of spreading on its turn
     * @param randomUtils the randomness used to roll the chance
     * @param areaFactory builds the Area of tiles searched for a spot to spread onto
     */
    public SelfReplicate(int intervalTurns, int chancePercent, RandomUtils randomUtils, AreaFactory areaFactory) {
        super(intervalTurns, chancePercent, randomUtils);
        this.areaFactory = areaFactory;
    }

    /**
     * Plants a new plant on a random adjacent plantable tile.
     *
     * @param plant the plant that spreads
     * @param location the location of the plant
     * @return a description of the spreading, or an empty string if the
     *         plant cannot spread or no adjacent tile can be planted on
     */
    @Override
    protected String perform(Plant plant, Location location) {
        Spreading spreading = location.getGroundAs(Spreading.class);
        if (spreading == null) {
            return "";
        }
        Optional<Location> target = areaFactory.adjacentTo(location).filter(new PlantableTile()).randomTile();
        if (target.isEmpty()) {
            return "";
        }
        Plant child = spreading.offspring();
        target.get().getGroundAs(Plantable.class).plant(child, target.get());
        return plant + " at " + location + " spreads, and a " + child
                + " takes root at " + target.get() + ".";
    }
}
