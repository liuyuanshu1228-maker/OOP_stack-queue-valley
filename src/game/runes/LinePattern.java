package game.runes;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Three rune stones in a straight line. The line may run along any of the
 * four axes (horizontal, vertical, or either diagonal), and the just-placed
 * stone ({@code center}) may be the middle stone or either end stone -- the
 * same finished line triggers no matter which of the three stones was placed
 * last. The effect reaches the full 5-tile line; completing it also pushes
 * any Actor standing on the line one tile to the side.
 */
public class LinePattern implements RunePattern {

    /** How many tiles the line extends on each side of its true centre. */
    private static final int HALF_LENGTH = 2;

    /** The four axis directions a line can run along, as (dx, dy) one-tile steps. */
    private static final int[][] AXES = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};

    /**
     * The three roles {@code center} could be playing in the line: left end,
     * middle, or right end. Each row is
     * {otherStone1Offset, otherStone2Offset, trueCentreOffset}, all measured
     * in axis steps from {@code center}.
     */
    private static final int[][] ROLES = {
            {1, 2, 1},
            {-1, 1, 0},
            {-2, -1, -1}
    };

    @Override
    public boolean matches(Location center) {
        return findMatch(center) != null;
    }

    @Override
    public List<Location> affectedTiles(Location center) {
        Match match = findMatch(center);
        if (match == null) {
            return List.of();
        }
        List<Location> tiles = new ArrayList<>();
        for (int step = -HALF_LENGTH; step <= HALF_LENGTH; step++) {
            Location tile = RuneGeometry.offsetOrNull(
                    match.trueCenter(), match.axis()[0] * step, match.axis()[1] * step);
            if (tile != null) {
                tiles.add(tile);
            }
        }
        return tiles;
    }

    @Override
    public String applyBonusEffect(Location center, GameMap map) {
        Match match = findMatch(center);
        if (match == null) {
            return "The line has nothing to push.";
        }
        int[] perpendicular = {-match.axis()[1], match.axis()[0]};
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
     * Finds which axis, and which of the three roles, {@code center} is
     * playing in a completed line, if any. Every role on every axis is tried,
     * so this gives the same answer regardless of which of the line's three
     * stones was placed last. Recomputed on every call rather than cached, so
     * the answer also never depends on call order.
     *
     * @param center the tile whose rune stone was just placed
     * @return the matching axis and the line's true centre, or null if no line is completed
     */
    private Match findMatch(Location center) {
        for (int[] axis : AXES) {
            for (int[] role : ROLES) {
                Location other1 = RuneGeometry.offsetOrNull(center, axis[0] * role[0], axis[1] * role[0]);
                Location other2 = RuneGeometry.offsetOrNull(center, axis[0] * role[1], axis[1] * role[1]);
                if (RuneGeometry.hasRuneStone(other1) && RuneGeometry.hasRuneStone(other2)) {
                    Location trueCenter = RuneGeometry.offsetOrNull(center, axis[0] * role[2], axis[1] * role[2]);
                    if (trueCenter != null) {
                        return new Match(axis, trueCenter);
                    }
                }
            }
        }
        return null;
    }

    private record Match(int[] axis, Location trueCenter) {
    }
}
