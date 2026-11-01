package game.pests;

import edu.monash.fit2099.engine.positions.Location;
import game.plants.Regressible;
import game.positions.Plantable;
import game.positions.area.AreaFactory;
import game.positions.farm.Dirt;
import game.random.RandomUtils;

/**
 * A pest that burrows one or two tiles in a straight line, up, down, left or
 * right. When it feeds, the plant goes back one growth stage, and the bare
 * Soil on the four neighbouring tiles caves in and turns back into Dirt.
 */
public class BurrowingMole extends Pest {

    /** Builds the Area of tiles around this mole that may cave in. */
    private final AreaFactory areaFactory;

    /**
     * Creates a burrowing mole, shown as 'r', with 8 hit points. It burrows
     * one or two tiles up, down, left or right.
     *
     * @param randomUtils the randomness forwarded to this mole's fallback
     *                    wandering behaviour; this mole's own feeding has no
     *                    randomness of its own, so nothing is kept here
     * @param areaFactory builds the Area of tiles around this mole that may cave in
     */
    public BurrowingMole(RandomUtils randomUtils, AreaFactory areaFactory) {
        super("Burrowing Mole", 'r', 8, new int[][] {
                {0, -1}, {1, 0}, {0, 1}, {-1, 0},
                {0, -2}, {2, 0}, {0, 2}, {-2, 0}
        }, randomUtils, areaFactory);
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
        int cavedIn = 0;
        for (Location tile : areaFactory.adjacentTo(here).tiles()) {
            if (isStraightNeighbour(here, tile) && tile.getGroundAs(Plantable.class) != null) {
                tile.setGround(new Dirt());
                cavedIn++;
            }
        }
        if (cavedIn == 0) {
            return report;
        }
        return report + " " + this + " caves in the Soil around it, which turns back into Dirt.";
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
