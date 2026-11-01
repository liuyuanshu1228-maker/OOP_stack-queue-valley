package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Egg;
import game.positions.area.AreaFactory;
import game.positions.area.ItemDropper;
import game.positions.area.TileListDescription;
import game.random.RandomUtils;
import game.statistics.StatisticAdjuster;
import game.statistics.StatisticChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Chicken that wanders the farm and can be fed, producing Eggs.
 */
public class Chicken extends Animal {

    /** The Chicken's starting (and maximum) hit points. */
    private static final int MAX_HP = 10;

    /** HP restored to the Chicken when it is fed. */
    private static final int HEAL_AMOUNT = 3;

    /** Chance (out of 100) that a second Egg is dropped. */
    private static final int BONUS_EGG_CHANCE_PERCENT = 50;

    /** The randomness used to roll the bonus Egg chance. */
    private final RandomUtils randomUtils;

    /** Builds the Area the Eggs are dropped onto. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new Chicken.
     *
     * @param name the name of the Chicken
     * @param randomUtils the randomness used to roll the bonus Egg chance
     * @param areaFactory builds the Area the Eggs are dropped onto
     */
    public Chicken(String name, RandomUtils randomUtils, AreaFactory areaFactory) {
        super(name, 'c', MAX_HP, randomUtils);
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * Applies the effect of feeding this Chicken: restores 3 HP (without
     * exceeding its maximum HP) and drops one Egg on a tile adjacent to the
     * Farmer, with a 50% chance of a second Egg being dropped as well.
     *
     * @param farmer the Actor doing the feeding
     * @param farmerLocation the Location of the Farmer
     * @return a description of the outcome, to be displayed to the player
     */
    @Override
    public String feed(Actor farmer, Location farmerLocation) {
        StatisticChange healed = new StatisticAdjuster(this).increase(ActorStatistics.HEALTH, HEAL_AMOUNT);

        ItemDropper dropper = new ItemDropper(areaFactory.adjacentTo(farmerLocation));
        List<Location> eggTiles = new ArrayList<>();
        dropEgg(dropper, eggTiles);
        if (randomUtils.rollPercent(BONUS_EGG_CHANCE_PERCENT)) {
            dropEgg(dropper, eggTiles);
        }

        return this + " clucks happily, recovers " + healed.describe("HP") + ", and lays "
                + eggTiles.size() + " eggs at " + new TileListDescription().of(eggTiles) + ".";
    }

    /**
     * Lays one Egg on a random tile of the dropper's area and records where it landed.
     *
     * @param dropper places the Egg on a tile next to the Farmer
     * @param eggTiles the tiles Eggs have landed on so far; the new tile is added
     */
    private void dropEgg(ItemDropper dropper, List<Location> eggTiles) {
        Optional<Location> tile = dropper.dropOnRandomTile(layEgg());
        if (tile.isPresent()) {
            eggTiles.add(tile.get());
        }
    }

    /**
     * Creates an Egg that may hatch into a new Chicken.
     *
     * @return a new Chicken Egg
     */
    private Egg layEgg() {
        return new Egg(new ChickenFactory(randomUtils, areaFactory), randomUtils, areaFactory);
    }
}
