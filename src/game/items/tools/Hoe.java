package game.items.tools;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.enchantments.Enchantment;
import game.positions.Tillable;
import game.positions.area.Area;
import game.positions.area.TileListDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * A Hoe that the Farmer can carry and use to till dirt (and potentially other
 * tillable ground) into soil.
 * <p>
 * While enchanted, tilling a tile also tills every tillable tile adjacent to
 * it, then fires the attached Enchantment against every tile the till
 * touched (the original tile is handled by {@link game.actions.TillAction}
 * itself, not here, since this effect only concerns the extra spread).
 */
public class Hoe extends AbstractEnchantableTool implements TillingTool {

    /** Stamina cost of tilling something with this hoe. */
    private static final int STAMINA_COST = 2;

    /**
     * Creates a new Hoe.
     *
     * @param offeredEnchantments the Enchantments this Hoe can be enchanted with
     */
    public Hoe(List<Enchantment> offeredEnchantments) {
        super("Hoe", 'h', offeredEnchantments);
        this.makePortable();
    }

    /**
     * @return the stamina cost of tilling with this hoe
     */
    @Override
    public int getStaminaCost() {
        return STAMINA_COST;
    }

    /**
     * A hoe can till every Tillable ground.
     *
     * @param target the ground the carrier wants to till
     * @return always true
     */
    @Override
    public boolean canTill(Tillable target) {
        return true;
    }

    /**
     * While enchanted, tills every tillable tile adjacent to the given
     * Location, then fires the attached Enchantment against the tiles that
     * spread to. Does nothing while unenchanted.
     *
     * @param actor the Actor who tilled
     * @param location the Location that was just tilled
     * @return a description of the spread and the fired Enchantment, or "" while unenchanted
     */
    @Override
    public String triggerEnchantment(Actor actor, Location location) {
        if (enchantmentSlot().isEmpty()) {
            return "";
        }
        List<Location> tilledTiles = new ArrayList<>();
        for (Location nearby : Area.adjacentTo(location).tiles()) {
            Tillable tillable = nearby.getGroundAs(Tillable.class);
            if (tillable != null) {
                nearby.setGround(tillable.getTillResult());
                tilledTiles.add(nearby);
            }
        }
        String spreadMessage = tilledTiles.isEmpty()
                ? "The enchantment finds no adjacent ground to till."
                : "The enchantment spreads the tilling to " + new TileListDescription().of(tilledTiles) + ".";
        String enchantmentMessage = enchantmentSlot().fire(tilledTiles, actor);
        return (spreadMessage + " " + enchantmentMessage).trim();
    }
}
