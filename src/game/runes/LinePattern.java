package game.runes;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Three rune stones in a straight line, with {@code center} as the middle
 * one. The line may run along any of the four axes (horizontal, vertical, or
 * either diagonal). The effect reaches the full 5-tile line; completing it
 * also pushes any Actor standing on the line one tile to the side.
 */
public class LinePattern implements RunePattern {

    /** How many tiles the line extends on each side of the centre. */
    private static final int HALF_LENGTH = 2;

    /** The four axis directions a line can run along, as (dx, dy) one-tile steps. */
    private static final int[][] AXES = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};

    @Override
    public boolean matches(Location center) {
        return matchingAxis(center) != null;
    }

    @Override
    public List<Location> affectedTiles(Location center) {
        int[] axis = matchingAxis(center);
        List<Location> tiles = new ArrayList<>();
        for (int step = -HALF_LENGTH; step <= HALF_LENGTH; step++) {
            Location tile = RuneGeometry.offsetOrNull(center, axis[0] * step, axis[1] * step);
            if (tile != null) {
                tiles.add(tile);
            }
        }
        return tiles;
    }

    @Override
    public String applyBonusEffect(Location center, GameMap map) {
        int[] axis = matchingAxis(center);
        int[] perpendicular = {-axis[1], axis[0]};
        int pushed = 0;
        for (Location tile : affectedTiles(center)) {
            Actor occupant = tile.getActor();
            if (occupant != null) {
                Location pushTo = RuneGeometry.offsetOrNull(tile, perpendicular[0], perpendicular[1]);
                if (pushTo != null && pushTo.canActorEnter(occupant)) {
                    map.moveActor(occupant, pushTo);
                    pushed++;
                }
            }
        }
        return "The line's shockwave pushes " + pushed + " actor(s) aside.";
    }

    /**
     * Finds which of the four axes has a rune stone on both sides of
     * {@code center}. Recomputed on every call rather than cached, so that
     * this pattern gives the same answer no matter what order its methods
     * are called in -- it does not depend on {@link #matches} having been
     * called first.
     *
     * @param center the tile to check around
     * @return the matching axis, or null if none of the four match
     */
    private int[] matchingAxis(Location center) {
        for (int[] axis : AXES) {
            Location positiveSide = RuneGeometry.offsetOrNull(center, axis[0], axis[1]);
            Location negativeSide = RuneGeometry.offsetOrNull(center, -axis[0], -axis[1]);
            if (RuneGeometry.hasRuneStone(positiveSide) && RuneGeometry.hasRuneStone(negativeSide)) {
                return axis;
            }
        }
        return null;
    }
}
