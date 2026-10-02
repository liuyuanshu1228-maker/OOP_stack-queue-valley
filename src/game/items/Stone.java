package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a Stone ('s') item that can be sold to the Merchant for 10 coins.
 * Selling a Stone also causes the Merchant to build a Ground on an adjacent tile.
 * The concrete Ground type is injected at construction time, so Stone has no
 * dependency on any specific Ground subclass.
 */
public class Stone extends SellableItem {

    private static final int SELL_PRICE = 10;

    private final Supplier<Ground> groundFactory;

    /**
     * Constructor for Stone.
     *
     * @param groundFactory a factory that produces the Ground placed when this Stone is sold
     */
    public Stone(Supplier<Ground> groundFactory) {
        super("Stone", 's', SELL_PRICE);
        this.groundFactory = groundFactory;
    }

    /**
     * Builds a new Ground on a randomly chosen tile adjacent to the Merchant.
     *
     * @param seller           the Actor selling this item
     * @param merchant         the Actor buying it
     * @param merchantLocation the Location of the merchant
     * @return a description of the Ground being built, or an empty string if there is no adjacent tile
     */
    @Override
    protected String applySaleEffect(Actor seller, Actor merchant, Location merchantLocation) {
        List<Location> nearby = merchantLocation.getNearbyLocations(1);
        if (nearby.isEmpty()) {
            return "";
        }
        Ground built = groundFactory.get();
        pickRandom(nearby).setGround(built);
        return merchant + " builds a " + built + " nearby.";
    }
}
