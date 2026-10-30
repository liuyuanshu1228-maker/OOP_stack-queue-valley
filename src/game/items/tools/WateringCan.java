package game.items.tools;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.enchantments.Enchantment;
import game.positions.Waterable;
import game.positions.area.AreaFactory;
import game.positions.area.WaterableTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Watering Can that the Farmer can carry and use to water dirt (and
 * potentially other waterable ground) into a puddle. Has an unlimited water
 * supply.
 * <p>
 * While enchanted, watering a tile also waters one other waterable tile
 * adjacent to it, chosen at random, then fires the attached Enchantment
 * against every tile the watering touched (the original tile is handled by
 * {@link game.actions.WaterAction} itself, not here, since this effect only
 * concerns the extra spread).
 */
public class WateringCan extends AbstractEnchantableTool implements WateringTool {

    /** Stamina cost of watering something with this can. */
    private static final int STAMINA_COST = 2;

    /** Builds the Area searched for an adjacent tile to also water. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new WateringCan.
     *
     * @param offeredEnchantments the Enchantments this WateringCan can be enchanted with
     * @param areaFactory builds the Area searched for an adjacent tile to also water
     */
    public WateringCan(List<Enchantment> offeredEnchantments, AreaFactory areaFactory) {
        super("Watering Can", 'w', offeredEnchantments);
        this.makePortable();
        this.areaFactory = areaFactory;
    }

    /**
     * @return the stamina cost of watering with this can
     */
    @Override
    public int getStaminaCost() {
        return STAMINA_COST;
    }

    /**
     * A watering can can water every Waterable ground.
     *
     * @param target the ground the carrier wants to water
     * @return always true
     */
    @Override
    public boolean canWater(Waterable target) {
        return true;
    }

    /**
     * While enchanted, waters one random waterable tile adjacent to the given
     * Location -- letting that Ground's own {@link Waterable#water} decide
     * what being watered does to it, the same way the original watering did
     * -- then fires the attached Enchantment against the tiles touched. Does
     * nothing while unenchanted.
     *
     * @param actor the Actor who watered
     * @param location the Location that was just watered
     * @return a description of the spread and the fired Enchantment, or "" while unenchanted
     */
    @Override
    public String triggerEnchantment(Actor actor, Location location) {
        if (enchantmentSlot().isEmpty()) {
            return "";
        }
        List<Location> affectedTiles = new ArrayList<>();
        String spreadMessage;
        Optional<Location> spot = areaFactory.adjacentTo(location).filter(new WaterableTile()).randomTile();
        if (spot.isPresent()) {
            Waterable waterable = spot.get().getGroundAs(Waterable.class);
            String waterReport = waterable.water(actor, spot.get());
            affectedTiles.add(spot.get());
            spreadMessage = "The enchantment also waters " + spot.get() + ": " + waterReport;
        } else {
            spreadMessage = "The enchantment finds no adjacent ground to water.";
        }
        String enchantmentMessage = enchantmentSlot().fire(affectedTiles, actor, location);
        return (spreadMessage + " " + enchantmentMessage).trim();
    }
}
