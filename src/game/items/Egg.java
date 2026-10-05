package game.items;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.LocationUtils;
import game.RandomUtils;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Represents an Egg ('0') item produced by a Chicken. Can be sold to the Merchant
 * for 40 coins, with a 50% chance to spawn an animal near the Merchant.
 * The concrete Actor type that spawns is injected at construction time, so Egg
 * has no dependency on any specific Actor subclass.
 */
public class Egg extends SellableItem {

    /** Coins paid by the Merchant when an Egg is sold. */
    private static final int SELL_PRICE = 40;
    /** Percentage chance that selling an Egg spawns a new animal near the Merchant. */
    private static final int ANIMAL_SPAWN_CHANCE_PERCENT = 50;

    /** Factory that produces the Actor spawned adjacent to the Merchant on a successful sale. */
    private final Supplier<Actor> animalFactory;

    /**
     * Constructor for Egg.
     *
     * @param animalFactory a factory that produces the Actor spawned when this Egg is sold
     */
    public Egg(Supplier<Actor> animalFactory) {
        super("Egg", '0', SELL_PRICE);
        this.animalFactory = animalFactory;
    }

    /**
     * Rolls a 50% chance for an animal to spawn on an enterable tile adjacent to the Merchant.
     * Nothing happens if there is no valid tile.
     *
     * @param seller           the Actor selling this item
     * @param merchant         the Actor buying it
     * @param merchantLocation the Location of the merchant
     * @return a description of the spawn, or an empty string if no animal appeared
     */
    @Override
    protected String applySaleEffect(Actor seller, Actor merchant, Location merchantLocation) {
        if (!RandomUtils.rollPercent(ANIMAL_SPAWN_CHANCE_PERCENT)) {
            return "";
        }
        Actor animal = animalFactory.get();
        Optional<Location> spot = LocationUtils.randomEnterableNearby(merchantLocation, animal);
        if (spot.isEmpty()) {
            return "";
        }
        try {
            spot.get().addActor(animal);
            return animal + " appears near " + merchant + ".";
        } catch (GameEngineException e) {
            return animal + " tries to appear near " + merchant + ", but there is no room for it.";
        }
    }
}
