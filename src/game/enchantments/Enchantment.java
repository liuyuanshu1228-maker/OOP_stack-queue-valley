package game.enchantments;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * A magical effect that can be attached to an {@link game.items.tools.Enchantable}
 * tool. Fires once, against every tile the tool's own effect touched, then is
 * spent.
 */
public interface Enchantment {

    /**
     * Applies this Enchantment's effect to every given tile.
     *
     * @param affectedTiles the tiles the host tool's own effect touched
     * @param activator the Actor who triggered the effect
     * @return a description of what happened, to be displayed to the player; never null
     */
    String applyTo(List<Location> affectedTiles, Actor activator);
}
