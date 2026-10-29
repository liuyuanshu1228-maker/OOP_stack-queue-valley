package game.positions.area;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Moves Items onto, and off, the tiles of an {@link Area}.
 */
public class ItemDropper {

    /** The area whose tiles receive the Items. */
    private final Area area;

    /**
     * Creates a dropper that places Items on the given area.
     *
     * @param area the area to place Items on
     */
    public ItemDropper(Area area) {
        this.area = area;
    }

    /**
     * Creates a dropper that places Items on the tiles adjacent to the given
     * Location (but not on the Location itself).
     *
     * @param centre the Location whose adjacent tiles receive the Items
     * @return a dropper for the tiles around the given Location
     */
    public static ItemDropper adjacentTo(Location centre) {
        return new ItemDropper(Area.adjacentTo(centre));
    }

    /**
     * Places the given Item on a randomly chosen tile of the area.
     * Does nothing if the area has no tiles.
     *
     * @param item the Item to place
     * @return the tile the Item was placed on, or empty if the area has no tiles
     */
    public Optional<Location> dropOnRandomTile(Item item) {
        Optional<Location> tile = area.randomTile();
        if (tile.isPresent()) {
            tile.get().addItem(item);
        }
        return tile;
    }

    /**
     * Removes every Item lying on the tiles of the area.
     *
     * @return the Items that were removed, in tile order
     */
    public List<Item> liftAll() {
        List<Item> lifted = new ArrayList<>();
        for (Location tile : area.tiles()) {
            for (Item item : new ArrayList<>(tile.getItems())) {
                tile.removeItem(item);
                lifted.add(item);
            }
        }
        return lifted;
    }

    /**
     * Removes every Item the given Actor is carrying and places each one on a
     * randomly chosen tile of the area.
     *
     * @param actor the Actor whose inventory is emptied
     * @return the Items that were dropped, in the order they were carried
     */
    public List<Item> dropAllCarriedBy(Actor actor) {
        List<Item> carried = new ArrayList<>(actor.getInventory().getItems());
        for (Item item : carried) {
            actor.getInventory().remove(item);
            dropOnRandomTile(item);
        }
        return carried;
    }
}
