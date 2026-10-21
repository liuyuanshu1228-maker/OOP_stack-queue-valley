package game.runes;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Three rune stones at three corners of a 2x2 block: {@code center}, the
 * tile east of it, and the tile south of it. The effect reaches the one
 * remaining corner -- the block's fourth tile -- which then rises into an
 * {@link Altar}.
 */
public class TrianglePattern implements RunePattern {

    private static final int EAST_DX = 1;
    private static final int EAST_DY = 0;
    private static final int SOUTH_DX = 0;
    private static final int SOUTH_DY = 1;
    private static final int INTERIOR_DX = 1;
    private static final int INTERIOR_DY = 1;

    @Override
    public boolean matches(Location center) {
        return RuneGeometry.hasRuneStone(RuneGeometry.offsetOrNull(center, EAST_DX, EAST_DY))
                && RuneGeometry.hasRuneStone(RuneGeometry.offsetOrNull(center, SOUTH_DX, SOUTH_DY));
    }

    @Override
    public List<Location> affectedTiles(Location center) {
        Location interior = RuneGeometry.offsetOrNull(center, INTERIOR_DX, INTERIOR_DY);
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
        return "The triangle's fourth corner rises into an Altar.";
    }
}
