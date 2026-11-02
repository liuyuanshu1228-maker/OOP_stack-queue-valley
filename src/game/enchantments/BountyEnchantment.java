package game.enchantments;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.tools.Enchantment;
import game.plants.Plant;

import java.util.ArrayList;
import java.util.List;

/**
 * An Enchantment that forces an extra growth attempt on any Plant occupying
 * a tile its host tool's own effect touched, by calling the Plant's own
 * {@link Plant#forceGrowth} directly. Unlike a plant's normal turn, this
 * growth skips whatever interval or chance a plant's own ability would
 * normally apply, so the attempt is not silently wasted on a turn the
 * ability was not due. The plant does not age, since this growth is
 * magically forced rather than a natural turn, but a plant whose growth is
 * paused (for example by a frost) still does nothing.
 */
public class BountyEnchantment implements Enchantment {

    /**
     * Creates a Bounty enchantment, which makes the plants on the affected tiles grow.
     */
    public BountyEnchantment() {
    }

    /**
     * Forces a growth attempt on the Plant occupying each given tile, if any.
     *
     * @param affectedTiles the tiles the host tool's own effect touched
     * @param activator the Actor who triggered the effect
     * @param origin the Location the host tool was used at
     * @return each plant's own report of what it did (for example, which stage it
     * grew into), each naming its tile, or a message naming the origin if no plant grew
     */
    @Override
    public String applyTo(List<Location> affectedTiles, Actor activator, Location origin) {
        List<String> growthReports = new ArrayList<>();
        for (Location tile : affectedTiles) {
            Plant plant = tile.getGroundAs(Plant.class);
            if (plant == null) {
                continue;
            }
            String growthReport = plant.forceGrowth(tile);
            if (!growthReport.isEmpty()) {
                growthReports.add(growthReport);
            }
        }
        if (growthReports.isEmpty()) {
            return "The Bounty Enchantment finds no plant ready to grow further near " + origin + ".";
        }
        return "The Bounty Enchantment makes the plants grow further: " + String.join(" ", growthReports);
    }

    @Override
    public String toString() {
        return "Bounty Enchantment";
    }
}
