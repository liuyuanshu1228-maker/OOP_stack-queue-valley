package game.enchantments;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.plants.Plant;
import game.positions.area.TileListDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * An Enchantment that forces an extra growth attempt on any Plant occupying
 * a tile its host tool's own effect touched, by calling the Plant's own
 * {@link Plant#attemptGrowth} directly (bypassing the normal per-turn growth
 * gate, since this growth is magically forced rather than a natural turn).
 */
public class BountyEnchantment implements Enchantment {

    /**
     * Forces a growth attempt on the Plant occupying each given tile, if any.
     *
     * @param affectedTiles the tiles the host tool's own effect touched
     * @param activator the Actor who triggered the effect
     * @return a description naming exactly which tiles had a plant grow further
     */
    @Override
    public String applyTo(List<Location> affectedTiles, Actor activator) {
        List<Location> grownTiles = new ArrayList<>();
        for (Location tile : affectedTiles) {
            Plant plant = tile.getGroundAs(Plant.class);
            if (plant == null) {
                continue;
            }
            String growthReport = plant.attemptGrowth(tile);
            if (!growthReport.isEmpty()) {
                grownTiles.add(tile);
            }
        }
        if (grownTiles.isEmpty()) {
            return "The Bounty Enchantment finds no plant ready to grow further.";
        }
        return "Plants grow further at " + new TileListDescription().of(grownTiles) + ".";
    }

    @Override
    public String toString() {
        return "Bounty Enchantment";
    }
}
