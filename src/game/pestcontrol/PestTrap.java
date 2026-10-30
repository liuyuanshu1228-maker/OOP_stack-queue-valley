package game.pestcontrol;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.plants.Plant;
import game.positions.area.AreaFactory;

/**
 * A pest trap the Farmer sets on the tile they stand on, as long as no plant
 * grows there. The tile becomes a {@link TrapGround}.
 */
public class PestTrap extends DeterrentItem {

    /** The name of this item. */
    private static final String NAME = "Pest Trap";

    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = '[';

    /** Builds the Areas the set TrapGround searches for pests to pull and capture. */
    private final AreaFactory areaFactory;

    /**
     * Creates a pest trap.
     *
     * @param areaFactory builds the Areas the set TrapGround searches for pests to pull and capture
     */
    public PestTrap(AreaFactory areaFactory) {
        super(NAME, DISPLAY_CHAR);
        this.areaFactory = areaFactory;
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
        here.setGround(new TrapGround(here.getGround(), areaFactory));
        return "A pest trap is set at " + here + ".";
    }
}
