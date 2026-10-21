package game.runes;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Three rune stones at three corners of a 2x2 block, with {@code center} as
 * any one of the three -- the same finished triangle triggers no matter
 * which corner's stone was placed last. The effect reaches the one corner
 * left empty, which then rises into an {@link Altar}.
 */
public class TrianglePattern implements RunePattern {

    /**
     * The other two corners of the block, and the empty (interior) corner,
     * relative to {@code center}, for each of the four corners {@code center}
     * could be playing. Each row is {otherCorner1, otherCorner2, interiorCorner}.
     */
    private static final int[][][] ROLES = {
            // center = north-west; others = north-east, south-west; interior = south-east
            {{1, 0}, {0, 1}, {1, 1}},
            // center = north-east; others = north-west, south-east; interior = south-west
            {{-1, 0}, {0, 1}, {-1, 1}},
            // center = south-west; others = north-west, south-east; interior = north-east
            {{0, -1}, {1, 0}, {1, -1}},
            // center = south-east; others = north-east, south-west; interior = north-west
            {{0, -1}, {-1, 0}, {-1, -1}}
    };

    @Override
    public boolean matches(Location center) {
        return matchingRole(center) != null;
    }

    @Override
    public List<Location> affectedTiles(Location center) {
        int[][] role = matchingRole(center);
        if (role == null) {
            return List.of();
        }
        Location interior = RuneGeometry.offsetOrNull(center, role[2][0], role[2][1]);
        return interior == null ? List.of() : List.of(interior);
    }

    @Override
    public String applyBonusEffect(Location center, GameMap map) {
        List<Location> interior = affectedTiles(center);
        if (interior.isEmpty()) {
            return "The triangle has no room to raise an Altar.";
        }
        Location altarTile = interior.get(0);
        altarTile.setGround(new Altar());
        return "The triangle's empty corner rises into an Altar.";
    }

    /**
     * Finds which of the four corners {@code center} is playing in a
     * completed triangle, if any. Every corner is tried, so this gives the
     * same answer regardless of which of the triangle's three stones was
     * placed last. Recomputed on every call rather than cached, so the answer
     * also never depends on call order.
     *
     * @param center the tile whose rune stone was just placed
     * @return the matching role, or null if no triangle is completed
     */
    private int[][] matchingRole(Location center) {
        for (int[][] role : ROLES) {
            Location other1 = RuneGeometry.offsetOrNull(center, role[0][0], role[0][1]);
            Location other2 = RuneGeometry.offsetOrNull(center, role[1][0], role[1][1]);
            if (RuneGeometry.hasRuneStone(other1) && RuneGeometry.hasRuneStone(other2)) {
                return role;
            }
        }
        return null;
    }
}
