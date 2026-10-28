package game.pests;

import edu.monash.fit2099.engine.positions.Location;
import game.crops.Unplanted;
import game.plants.Regressible;
import game.positions.Plantable;
import game.positions.Tillable;
import game.positions.area.Area;

/**
 * A slow pest that crawls one tile at a time. When it feeds, the plant goes
 * back one growth stage, and the bare farm ground around it is covered with
 * slime, so nothing can be planted there for a while.
 */
public class SlimeSnail extends Pest {

    /** The name of this pest. */
    private static final String NAME = "Slime Snail";

    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = 'q';

    /** The snail's starting hit points. */
    private static final int HIT_POINTS = 5;

    /** A crawl: any of the eight neighbouring tiles. */
    private static final int[][] CRAWL_OFFSETS = {
            {-1, -1}, {0, -1}, {1, -1},
            {-1, 0}, {1, 0},
            {-1, 1}, {0, 1}, {1, 1}
    };

    /**
     * Creates a slime snail.
     */
    public SlimeSnail() {
        super(NAME, DISPLAY_CHAR, HIT_POINTS, CRAWL_OFFSETS);
    }

    /**
     * Sets the plant here back one growth stage, then covers the bare farm
     * ground around it with slime.
     *
     * @param here the tile this snail stands on
     * @return what happened, or an empty string if there is no plant here
     */
    @Override
    public String feedOn(Location here) {
        Regressible plant = here.getGroundAs(Regressible.class);
        if (plant == null) {
            return "";
        }
        String report = plant.regress(here);
        int slimed = 0;
        for (Location tile : Area.adjacentTo(here).filter(new Unplanted()).tiles()) {
            if (isBareFarmGround(tile)) {
                tile.setGround(new SlimeGround(tile.getGround()));
                slimed++;
            }
        }
        if (slimed == 0) {
            return report;
        }
        return report + " " + this + " leaves slime on the bare ground around it.";
    }

    /**
     * @param tile the tile to test
     * @return true if the tile is bare Soil or Dirt, which slime can cover
     */
    private boolean isBareFarmGround(Location tile) {
        return tile.getGroundAs(Plantable.class) != null
                || tile.getGroundAs(Tillable.class) != null;
    }
}
