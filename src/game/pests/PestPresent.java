package game.pests;

import edu.monash.fit2099.engine.positions.Location;
import game.positions.area.TileCondition;

/**
 * Met by the tiles where a pest is standing.
 * <p>
 * A pest is any actor with the {@link Infestation} capability, so this works
 * for every pest without naming one, and without {@code instanceof}. The
 * defences share this one condition instead of each writing its own check.
 * <p>
 * It lives in the pests package because "what counts as a pest" is knowledge
 * of this package, and the area package must not depend on it.
 */
public class PestPresent implements TileCondition {

    /**
     * Creates a condition that is met by tiles where a pest stands.
     */
    public PestPresent() {
    }

    /**
     * @param tile the tile to test
     * @return true if an actor stands on the tile and that actor is a pest
     */
    @Override
    public boolean isMetBy(Location tile) {
        if (!tile.containsAnActor()) {
            return false;
        }
        return tile.getActor().asCapability(Infestation.class).isPresent();
    }
}
