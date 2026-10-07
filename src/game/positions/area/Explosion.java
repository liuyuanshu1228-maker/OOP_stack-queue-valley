package game.positions.area;

import edu.monash.fit2099.engine.positions.Location;

/**
 * An explosion that hurts every Actor standing in an {@link Area}.
 */
public class Explosion {

    /** The damage dealt to each Actor caught in the explosion. */
    private final int damage;

    /**
     * Creates an explosion.
     *
     * @param damage the damage dealt to each Actor caught in it
     */
    public Explosion(int damage) {
        this.damage = damage;
    }

    /**
     * Hurts the Actor (if any) standing on each tile of the area.
     *
     * @param area the area the explosion covers
     */
    public void detonateOn(Area area) {
        for (Location tile : area.tiles()) {
            if (tile.containsAnActor()) {
                tile.getActor().hurt(damage);
            }
        }
    }
}
