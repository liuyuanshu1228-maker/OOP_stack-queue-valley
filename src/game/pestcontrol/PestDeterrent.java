package game.pestcontrol;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A defence that acts on the pests around a tile, such as a trap or a bag of
 * salt.
 * <p>
 * The action that deploys a defence depends only on this interface, so a new
 * defence needs no change to that action.
 */
public interface PestDeterrent {

    /**
     * Acts on the pests around the given tile.
     *
     * @param here the tile the defence is used on
     * @param map the map containing that tile
     * @return a description of what happened, or an empty string if nothing
     * happened
     */
    public String deterAround(Location here, GameMap map);
}
