package game.runes;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Four rune stones at the four corners of a 3x3 block, with {@code center}
 * as any one of them. The effect reaches the block's centre tile. Completing
 * it also plants a {@link Thorn} one tile beyond each of the four corners.
 */
public class SquarePattern implements RunePattern {

    /**
     * The three other corners of the block, relative to {@code center},
     * for each of the four roles {@code center} could be playing (top-left,
     * top-right, bottom-left, bottom-right of the block).
     */
    private static final int[][][] OTHER_CORNERS_BY_ROLE = {
            {{2, 0}, {0, 2}, {2, 2}},
            {{-2, 0}, {-2, 2}, {0, 2}},
            {{0, -2}, {2, -2}, {2, 0}},
            {{-2, -2}, {0, -2}, {-2, 0}}
    };

    @Override
    public boolean matches(Location center) {
        return matchingCorners(center) != null;
    }

    @Override
    public List<Location> affectedTiles(Location center) {
        List<Location> corners = matchingCorners(center);
        if (corners == null) {
            return List.of();
        }
        Box box = Box.enclosing(corners);
        Location interior = center.map().at((box.minX + box.maxX) / 2, (box.minY + box.maxY) / 2);
        return List.of(interior);
    }

    @Override
    public String applyBonusEffect(Location center, GameMap map) {
        List<Location> corners = matchingCorners(center);
        if (corners == null) {
            return "The square has no room to plant Thorns.";
        }
        Box box = Box.enclosing(corners);
        int planted = 0;
        planted += plantThornBeyond(map, box.minX, box.minY, -1, -1);
        planted += plantThornBeyond(map, box.maxX, box.minY, 1, -1);
        planted += plantThornBeyond(map, box.minX, box.maxY, -1, 1);
        planted += plantThornBeyond(map, box.maxX, box.maxY, 1, 1);
        return "The square's corners sprout " + planted + " Thorn tile(s).";
    }

    /**
     * Plants a Thorn one tile beyond the given corner, in the direction
     * (dx, dy) pointing away from the block, if that tile is on the map.
     *
     * @return 1 if a Thorn was planted, 0 if the tile was off the map
     */
    private int plantThornBeyond(GameMap map, int cornerX, int cornerY, int dx, int dy) {
        Location beyond = RuneGeometry.offsetOrNull(map.at(cornerX, cornerY), dx, dy);
        if (beyond == null) {
            return 0;
        }
        beyond.setGround(new Thorn(beyond.getGround()));
        return 1;
    }

    /**
     * Finds which of the four roles {@code center} could be playing matches
     * -- i.e. has rune stones on the other three corners of that role's
     * block. Recomputed on every call rather than cached, so this pattern's
     * answer never depends on call order.
     *
     * @param center the tile to check around
     * @return the four corner locations (center first), or null if no role matches
     */
    private List<Location> matchingCorners(Location center) {
        for (int[][] role : OTHER_CORNERS_BY_ROLE) {
            Location corner1 = RuneGeometry.offsetOrNull(center, role[0][0], role[0][1]);
            Location corner2 = RuneGeometry.offsetOrNull(center, role[1][0], role[1][1]);
            Location corner3 = RuneGeometry.offsetOrNull(center, role[2][0], role[2][1]);
            if (RuneGeometry.hasRuneStone(corner1) && RuneGeometry.hasRuneStone(corner2)
                    && RuneGeometry.hasRuneStone(corner3)) {
                return List.of(center, corner1, corner2, corner3);
            }
        }
        return null;
    }

    /**
     * The axis-aligned bounding box of a set of locations, used to find the
     * 3x3 block's centre and corners regardless of which corner
     * {@code center} happened to be.
     */
    private record Box(int minX, int maxX, int minY, int maxY) {
        static Box enclosing(List<Location> locations) {
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            for (Location location : locations) {
                minX = Math.min(minX, location.x());
                maxX = Math.max(maxX, location.x());
                minY = Math.min(minY, location.y());
                maxY = Math.max(maxY, location.y());
            }
            return new Box(minX, maxX, minY, maxY);
        }
    }
}
