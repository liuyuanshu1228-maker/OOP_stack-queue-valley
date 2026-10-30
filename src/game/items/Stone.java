package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.MerchantContext;
import game.positions.area.AreaFactory;

import java.util.Optional;

/**
 * Represents a Stone ('*') item that can be sold to the Merchant for 10 coins.
 * Selling a Stone also causes the Merchant to build a Ground on an adjacent tile.
 * The concrete Ground type is injected at construction time, so Stone has no
 * dependency on any specific Ground subclass.
 */
public class Stone extends SellableItem {

    /** Coins paid by the Merchant when a Stone is sold. */
    private static final int SELL_PRICE = 10;

    /**
     * Factory that produces the Ground instance placed on a tile adjacent to the Merchant
     * after the sale.
     */
    private final GroundFactory groundFactory;

    /** Builds the Area searched for a tile to build the new Ground on. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new Stone.
     *
     * @param groundFactory a factory that produces the Ground placed when this Stone is sold
     * @param areaFactory builds the Area searched for a tile to build the new Ground on
     */
    public Stone(GroundFactory groundFactory, AreaFactory areaFactory) {
        super("Stone", '*', SELL_PRICE);
        this.groundFactory = groundFactory;
        this.areaFactory = areaFactory;
    }

    /**
     * Builds a new Ground on a randomly chosen tile adjacent to the Merchant.
     *
     * @param seller the Actor selling this item
     * @param merchantContext the Merchant buying it and its current Location
     * @return a description of the Ground being built, or an empty string if there is no
     * adjacent tile
     */
    @Override
    protected String applySaleEffect(Actor seller, MerchantContext merchantContext) {
        Optional<Location> spot = areaFactory.adjacentTo(merchantContext.getLocation()).randomTile();
        if (spot.isEmpty()) {
            return "";
        }
        Ground built = groundFactory.create();
        spot.get().setGround(built);
        return merchantContext.getMerchant() + " builds a " + built + " nearby.";
    }
}
