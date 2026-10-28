package game.pestcontrol;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.plants.Plant;

/**
 * A pest trap the Farmer sets on the tile they stand on, as long as no plant
 * grows there. The tile becomes a {@link TrapGround}.
 */
public class PestTrap extends DeterrentItem {

    /** The name of this item. */
    private static final String NAME = "Pest Trap";

    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = '[';

    /**
     * Creates a pest trap.
     */
    public PestTrap() {
        super(NAME, DISPLAY_CHAR);
    }

    /**
     * A trap cannot be set on a plant.
     *
     * @param here the tile the owner stands on
     * @return true if no plant grows on that tile
     */
    @Override
    protected boolean canDeployAt(Location here) {
        return here.getGroundAs(Plant.class) == null;
    }

    /**
     * Turns the tile into a set trap.
     *
     * @param here the tile the user stands on
     * @param map the map containing that tile
     * @return what happened
     */
    @Override
    public String deterAround(Location here, GameMap map) {
        here.setGround(new TrapGround(here.getGround()));
        return "A pest trap is set at " + here + ".";
    }
}
