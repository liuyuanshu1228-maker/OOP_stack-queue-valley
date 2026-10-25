package game.plants;

import edu.monash.fit2099.engine.positions.Location;
import game.positions.Plantable;
import game.positions.area.Area;
import game.positions.area.PlantableTile;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A plant ability that plants a new plant on a random adjacent tile that can
 * be planted on (see {@link Plantable}).
 * <p>
 * The new plant may be the same kind as this one or an earlier stage, e.g.
 * {@code new SelfReplicate(5, StrawberrySeedling::new)}. If no adjacent tile
 * can be planted on, nothing happens.
 */
public class SelfReplicate extends PeriodicAbility {

    /**
     * Creates the plant to be planted next to this one.
     */
    private final Supplier<Plant> offspring;

    /**
     * Creates a SelfReplicate ability that always happens on its turn.
     *
     * @param intervalTurns how many turns of age between attempts to spread
     * @param offspring creates the plant to be planted next to this one
     */
    public SelfReplicate(int intervalTurns, Supplier<Plant> offspring) {
        super(intervalTurns);
        this.offspring = offspring;
    }

    /**
     * Creates a SelfReplicate ability with the given chance.
     *
     * @param intervalTurns how many turns of age between attempts to spread
     * @param chancePercent the chance, as a percentage, of spreading on its turn
     * @param offspring creates the plant to be planted next to this one
     */
    public SelfReplicate(int intervalTurns, int chancePercent, Supplier<Plant> offspring) {
        super(intervalTurns, chancePercent);
        this.offspring = offspring;
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
        Plant child = offspring.get();
        target.get().getGroundAs(Plantable.class).plant(child, target.get());
        return plant + " at " + location + " spreads, and a " + child
                + " takes root at " + target.get() + ".";
    }
}
