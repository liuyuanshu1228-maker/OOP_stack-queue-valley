package game.plants;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.area.ItemDropper;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A plant ability that drops one of the plant's produce on a random adjacent
 * tile.
 * <p>
 * The produce is given as a {@link Supplier}, so a new Item is created every
 * time, e.g. {@code new Produce(5, StrawberryItem::createRandom)}.
 */
public class Produce extends PeriodicAbility {

    /**
     * Creates the Item this plant drops.
     */
    private final Supplier<Item> produce;

    /**
     * Creates a Produce ability that always happens on its turn.
     *
     * @param intervalTurns how many turns of age between drops
     * @param produce creates the Item this plant drops
     */
    public Produce(int intervalTurns, Supplier<Item> produce) {
        super(intervalTurns);
        this.produce = produce;
    }

    /**
     * Drops a new Item on a random adjacent tile.
     *
     * @param plant the plant that produces
     * @param location the location of the plant
     * @return a description of the drop, or an empty string if there is no
     *         adjacent tile
     */
    @Override
    protected String perform(Plant plant, Location location) {
        Item item = produce.get();
        Optional<Location> tile = ItemDropper.adjacentTo(location).dropOnRandomTile(item);
        if (tile.isEmpty()) {
            return "";
        }
        return plant + " at " + location + " drops a " + item + " at " + tile.get() + ".";
    }
}
