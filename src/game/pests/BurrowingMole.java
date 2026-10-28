package game.pests;

import edu.monash.fit2099.engine.positions.Location;
import game.plants.Regressible;
import game.positions.Plantable;
import game.positions.area.Area;
import game.positions.farm.Dirt;

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

    /**
     * Creates a burrowing mole.
     */
    public BurrowingMole() {
        super(NAME, DISPLAY_CHAR, HIT_POINTS, BURROW_OFFSETS);
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
        int cavedIn = 0;
        for (Location tile : Area.adjacentTo(here).tiles()) {
            if (isStraightNeighbour(here, tile) && tile.getGroundAs(Plantable.class) != null) {
                tile.setGround(new Dirt());
                cavedIn++;
            }
        }
        return report + " " + this + " turns " + cavedIn + " Soil tiles around it back into Dirt.";
    }

    /**
     * @param here the tile this mole stands on
     * @param tile a tile next to here
     * @return true if the tile is directly above, below, left or right of here
     */
    private boolean isStraightNeighbour(Location here, Location tile) {
        return tile.x() == here.x() || tile.y() == here.y();
    }
}
