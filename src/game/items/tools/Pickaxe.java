package game.items.tools;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.enchantments.Enchantment;
import game.positions.Breakable;
import game.positions.area.AreaFactory;
import game.positions.area.BreakableTile;
import game.random.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A pickaxe that the Farmer can carry and use to break rocks, copper nodes and
 * geodes.
 * <p>
 * While enchanted, breaking something has a chance of the magic chaining to
 * one other breakable tile adjacent to it, chosen at random, breaking that
 * one too, then firing the attached Enchantment against every tile the chain
 * touched (the original tile is handled by {@link game.actions.BreakAction}
 * itself, not here, since this effect only concerns the extra chain).
 */
public class Pickaxe extends AbstractEnchantableTool implements BreakingTool {

    /** Chance, out of 100, that the chain-break effect spreads to an adjacent tile. */
    private static final int CHAIN_BREAK_CHANCE_PERCENT = 40;

    /** The randomness used to roll the chain-break chance. */
    private final RandomUtils randomUtils;

    /** Builds the Area searched for a tile to chain the break to. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new Pickaxe.
     *
     * @param offeredEnchantments the Enchantments this Pickaxe can be enchanted with
     * @param randomUtils the randomness used to roll the chain-break chance
     * @param areaFactory builds the Area searched for a tile to chain the break to
     */
    public Pickaxe(List<Enchantment> offeredEnchantments, RandomUtils randomUtils, AreaFactory areaFactory) {
        super("Pickaxe", 'x', offeredEnchantments);
        this.makePortable();
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * A pickaxe can break every Breakable thing: rocks, copper nodes and geodes.
     *
     * @param target the thing the carrier wants to break
     * @return always true
     */
    @Override
    public boolean canBreak(Breakable target) {
        return true;
    }

    /**
     * While enchanted, rolls a chance to chain the break to one random
     * breakable tile adjacent to the given Location, then fires the attached
     * Enchantment against the tiles the chain touched. Does nothing while
     * unenchanted.
     *
     * @param actor the Actor who broke something
     * @param location the Location of the thing that was just broken
     * @return a description of the chain and the fired Enchantment, or "" while unenchanted
     */
    @Override
    public String triggerEnchantment(Actor actor, Location location) {
        if (enchantmentSlot().isEmpty()) {
            return "";
        }
        List<Location> affectedTiles = new ArrayList<>();
        String chainMessage = "";
        if (randomUtils.rollPercent(CHAIN_BREAK_CHANCE_PERCENT)) {
            Optional<Location> spot = areaFactory.adjacentTo(location).filter(new BreakableTile()).randomTile();
            if (spot.isPresent()) {
                Breakable breakable = spot.get().getGroundAs(Breakable.class);
                String breakReport = breakable.breakUp(actor, spot.get());
                affectedTiles.add(spot.get());
                chainMessage = "The enchantment's energy arcs to " + spot.get() + ": " + breakReport;
            }
        }
        String enchantmentMessage = enchantmentSlot().fire(affectedTiles, actor, location);
        return (chainMessage + " " + enchantmentMessage).trim();
    }
}
