package game.crops;

import edu.monash.fit2099.engine.positions.Location;
import game.plants.Plant;
import game.positions.area.TileCondition;

/**
 * Met by the tiles where no plant is growing, at any growth stage.
 * <p>
 * It checks for {@link Plant} rather than {@link Crop}, so seedlings and
 * sprouts also count as planted tiles: for example, a watered Cauliflower
 * does not flood them into puddles.
 * <p>
 * It lives in the crops package because "what counts as a plant" is knowledge
 * of the plant packages, which the area package must not depend on.
 */
public class Unplanted implements TileCondition {

    /**
     * Creates a new Unplanted condition.
     */
    public Unplanted() {
    }

    /**
     * Checks whether no plant, at any growth stage, is growing on the tile.
     *
     * @param tile the tile to test
     * @return true if the ground on the tile is not a plant
     */
    @Override
    public boolean isMetBy(Location tile) {
        return tile.getGroundAs(Plant.class) == null;
    }
}
