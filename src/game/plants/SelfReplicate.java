package game.plants;

import edu.monash.fit2099.engine.positions.Location;
import game.positions.Plantable;
import game.positions.area.Area;
import game.positions.area.PlantableTile;

import java.util.Optional;

/**
 * A plant ability that plants a new plant on a random adjacent tile that can
 * be planted on (see {@link Plantable}).
 * <p>
 * The new plant is created by the plant itself, via {@link Plant#offspring()},
 * and may be the same kind as this one or an earlier stage, e.g. a
 * {@code StrawberrySprout}'s {@code offspring()} returns a new
 * {@code StrawberrySeedling}. If no adjacent tile can be planted on, nothing
 * happens.
 */
public class SelfReplicate extends PeriodicAbility {

    /**
     * Creates a SelfReplicate ability that always happens on its turn.
     *
     * @param intervalTurns how many turns of age between attempts to spread
     */
    public SelfReplicate(int intervalTurns) {
        super(intervalTurns);
    }

    /**
     * Creates a SelfReplicate ability with the given chance.
     *
     * @param intervalTurns how many turns of age between attempts to spread
     * @param chancePercent the chance, as a percentage, of spreading on its turn
     */
    public SelfReplicate(int intervalTurns, int chancePercent) {
        super(intervalTurns, chancePercent);
    }

    /**
     * Plants a new plant on a random adjacent plantable tile.
     *
     * @param plant the plant that spreads
     * @param location the location of the plant
     * @return a description of the spreading, or an empty string if no
     *         adjacent tile can be planted on
     */
    @Override
    protected String perform(Plant plant, Location location) {
        Optional<Location> target = Area.adjacentTo(location).filter(new PlantableTile()).randomTile();
        if (target.isEmpty()) {
            return "";
        }
        Plant child = plant.offspring();
        target.get().getGroundAs(Plantable.class).plant(child, target.get());
        return plant + " at " + location + " spreads, and a " + child
                + " takes root at " + target.get() + ".";
    }
}
