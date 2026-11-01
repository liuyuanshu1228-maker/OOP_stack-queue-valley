package game.pests;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.Location;
import game.plants.Regressible;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A swarm of locusts that jumps like a chess knight. When it feeds, every
 * plant on its tile and around it goes back one growth stage. A swarm has a
 * limited number of meals. After a meal it may split off a smaller swarm, and
 * it flies away once it has eaten all its meals.
 */
public class LocustSwarm extends Pest {

    /** The fewest meals a swarm must have left to split. */
    private final int mealsNeededToSplit = 2;

    /** Chance (out of 100) that a swarm splits after a meal. */
    private final int splitChancePercent = 30;

    /** How many more meals this swarm can eat before it flies away. */
    private int mealsLeft;

    /** The randomness used to roll the split chance, and handed to a split-off swarm. */
    private final RandomUtils randomUtils;

    /** Builds the Areas used to find plants and a landing tile, and handed to a split-off swarm. */
    private final AreaFactory areaFactory;

    /**
     * Creates a full-sized locust swarm, which can eat 4 meals before it
     * flies away.
     *
     * @param randomUtils the randomness used to roll the split chance, and
     *                    handed to a split-off swarm
     * @param areaFactory builds the Areas used to find plants and a landing
     *                    tile, and handed to a split-off swarm
     */
    public LocustSwarm(RandomUtils randomUtils, AreaFactory areaFactory) {
        this(4, randomUtils, areaFactory);
    }

    /**
     * Creates a locust swarm that can eat the given number of meals. Used
     * when a swarm splits. A swarm is shown as 'z', has 3 hit points and
     * jumps like a chess knight: two tiles one way and one tile sideways.
     *
     * @param meals how many meals the swarm can eat
     * @param randomUtils the randomness used to roll the split chance, and
     *                    handed to a split-off swarm
     * @param areaFactory builds the Areas used to find plants and a landing
     *                    tile, and handed to a split-off swarm
     * @throws IllegalArgumentException if meals is less than 1
     */
    private LocustSwarm(int meals, RandomUtils randomUtils, AreaFactory areaFactory) {
        super("Locust Swarm", 'z', 3, new int[][] {
                {1, -2}, {2, -1}, {2, 1}, {1, 2},
                {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}
        }, randomUtils, areaFactory);
        if (meals < 1) {
            throw new IllegalArgumentException("A swarm needs at least one meal, got " + meals);
        }
        this.mealsLeft = meals;
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * A swarm can feed if there is a plant on its tile or next to it.
     *
     * @param here the tile this swarm stands on
     * @return true if there is a plant on or around this tile
     */
    @Override
    protected boolean canFeedAt(Location here) {
        return !plantTilesAround(here).isEmpty();
    }

    /**
     * Sets every plant on and around this tile back one growth stage. The
     * swarm then flies away if it has no meals left; otherwise it may split.
     *
     * @param here the tile this swarm stands on
     * @return what happened, or an empty string if there is no plant around
     */
    @Override
    public String feedOn(Location here) {
        List<Location> plantTiles = plantTilesAround(here);
        if (plantTiles.isEmpty()) {
            return "";
        }
        StringBuilder report = new StringBuilder(this + " descends on the plants around " + here + ".");
        for (Location tile : plantTiles) {
            report.append(" ").append(tile.getGroundAs(Regressible.class).regress(tile));
        }
        mealsLeft--;
        if (mealsLeft == 0) {
            here.map().removeActor(this);
            return report.append(" ").append(this).append(" is full and flies away.").toString();
        }
        return report.append(splitAt(here)).toString();
    }

    /**
     * Gives about half of this swarm's remaining meals to a new swarm on an
     * empty tile next to it, if the swarm is big enough and the chance rolls.
     *
     * @param here the tile this swarm stands on
     * @return what happened, or an empty string if the swarm did not split
     */
    private String splitAt(Location here) {
        if (mealsLeft < mealsNeededToSplit || !randomUtils.rollPercent(splitChancePercent)) {
            return "";
        }
        LocustSwarm offshoot = new LocustSwarm(mealsLeft / 2, randomUtils, areaFactory);
        Optional<Location> landing = areaFactory.adjacentTo(here).randomEnterableTile(offshoot);
        if (landing.isEmpty()) {
            return "";
        }
        try {
            landing.get().addActor(offshoot);
        } catch (GameEngineException e) {
            return "";
        }
        mealsLeft -= offshoot.mealsLeft;
        return " " + this + " splits, and a smaller swarm lands at " + landing.get() + ".";
    }

    /**
     * @param here the tile this swarm stands on
     * @return the tiles on and around here that hold a plant
     */
    private List<Location> plantTilesAround(Location here) {
        List<Location> plantTiles = new ArrayList<>();
        for (Location tile : areaFactory.centredOn(here).tiles()) {
            if (tile.getGroundAs(Regressible.class) != null) {
                plantTiles.add(tile);
            }
        }
        return plantTiles;
    }
}
