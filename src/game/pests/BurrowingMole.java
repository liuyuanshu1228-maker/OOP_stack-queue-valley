package game.pests;

import edu.monash.fit2099.engine.positions.Location;
import game.plants.Regressible;
import game.positions.Plantable;
import game.positions.area.AreaFactory;
import game.positions.area.TileListDescription;
import game.positions.farm.Dirt;
import game.random.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A pest that burrows one or two tiles in a straight line, up, down, left or
 * right. When it feeds, the plant goes back one growth stage, and the bare
 * Soil on the four neighbouring tiles caves in and turns back into Dirt.
 */
public class BurrowingMole extends Pest {

    /** The name of this pest. */
    private static final String NAME = "Burrowing Mole";

    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = 'r';

    /** The mole's starting hit points. */
    private static final int HIT_POINTS = 8;

    /** A burrow: one or two tiles up, down, left or right. */
    private static final int[][] BURROW_OFFSETS = {
            {0, -1}, {1, 0}, {0, 1}, {-1, 0},
            {0, -2}, {2, 0}, {0, 2}, {-2, 0}
    };

    /** Builds the Area of tiles around this mole that may cave in. */
    private final AreaFactory areaFactory;

    /**
     * Creates a burrowing mole.
     *
     * @param randomUtils the randomness forwarded to this mole's fallback
     *                    wandering behaviour; this mole's own feeding has no
     *                    randomness of its own, so nothing is kept here
     * @param areaFactory builds the Area of tiles around this mole that may cave in
     */
    public BurrowingMole(RandomUtils randomUtils, AreaFactory areaFactory) {
        super(NAME, DISPLAY_CHAR, HIT_POINTS, BURROW_OFFSETS, randomUtils, areaFactory);
        this.areaFactory = areaFactory;
    }

    /**
     * Sets the plant here back one growth stage, and turns the bare Soil on
     * the four neighbouring tiles back into Dirt.
     *
     * @param here the tile this mole stands on
     * @return what happened, or an empty string if there is no plant here
     */
    @Override
    public String feedOn(Location here) {
        Regressible plant = here.getGroundAs(Regressible.class);
        if (plant == null) {
            return "";
        }
        String report = plant.regress(here);
        List<Location> cavedIn = new ArrayList<>();
        for (Location tile : areaFactory.adjacentTo(here).tiles()) {
            if (isStraightNeighbour(here, tile) && tile.getGroundAs(Plantable.class) != null) {
                tile.setGround(new Dirt());
                cavedIn.add(tile);
            }
        }
        if (cavedIn.isEmpty()) {
            return report;
        }
        return report + " " + this + " caves in the Soil at " + new TileListDescription().of(cavedIn)
                + ", which turns back into Dirt.";
    }

    /**
     * Checks whether a tile is next to this mole in a straight line, not diagonally.
     *
     * @param here the tile this mole stands on
     * @param tile a tile next to here
     * @return true if the tile is directly above, below, left or right of here
     */
    private boolean isStraightNeighbour(Location here, Location tile) {
        return tile.x() == here.x() || tile.y() == here.y();
    }
}
