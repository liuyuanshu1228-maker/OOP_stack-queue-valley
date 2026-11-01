package game.plants;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.area.AreaFactory;
import game.positions.area.ItemDropper;
import game.random.RandomUtils;

import java.util.Optional;

/**
 * A plant ability that drops one of the plant's produce on a random adjacent
 * tile.
 * <p>
 * The produce is created by the plant itself, via {@link Producing#produce()},
 * so a new Item is created every time, e.g. a {@code Strawberry}'s
 * {@code produce()} returns a new (possibly rotten) {@code StrawberryItem}.
 * If the plant is not {@link Producing}, nothing happens.
 */
public class Produce extends PeriodicAbility {

    /** Builds the Area of tiles the produce is dropped onto. */
    private final AreaFactory areaFactory;

    /**
     * Creates a Produce ability that always happens on its turn.
     *
     * @param intervalTurns how many turns of age between drops
     * @param randomUtils the randomness used to roll the chance
     * @param areaFactory builds the Area of tiles the produce is dropped onto
     */
    public Produce(int intervalTurns, RandomUtils randomUtils, AreaFactory areaFactory) {
        super(intervalTurns, randomUtils);
        this.areaFactory = areaFactory;
    }

    /**
     * Drops a new Item on a random adjacent tile.
     *
     * @param plant the plant that produces
     * @param location the location of the plant
     * @return a description of the drop, or an empty string if the plant
     *         produces nothing or there is no adjacent tile
     */
    @Override
    protected String perform(Plant plant, Location location) {
        Producing producing = location.getGroundAs(Producing.class);
        if (producing == null) {
            return "";
        }
        Item item = producing.produce();
        Optional<Location> tile = new ItemDropper(areaFactory.adjacentTo(location)).dropOnRandomTile(item);
        if (tile.isEmpty()) {
            return "";
        }
        return plant + " at " + location + " drops a " + item + " at " + tile.get() + ".";
    }
}
