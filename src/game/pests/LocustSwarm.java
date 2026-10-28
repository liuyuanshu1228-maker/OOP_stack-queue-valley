package game.pests;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.Location;
import game.plants.Regressible;
import game.positions.area.Area;
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

    /** The name of this pest. */
    private static final String NAME = "Locust Swarm";

    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = 'z';

    /** The swarm's starting hit points. */
    private static final int HIT_POINTS = 3;

    /** How many meals a new swarm can eat before it flies away. */
    private static final int STARTING_MEALS = 4;

    /** The fewest meals a swarm must have left to split. */
    private static final int MEALS_NEEDED_TO_SPLIT = 2;

    /** Chance (out of 100) that a swarm splits after a meal. */
    private static final int SPLIT_CHANCE_PERCENT = 30;

    /** A knight's jump: two tiles one way and one tile sideways. */
    private static final int[][] JUMP_OFFSETS = {
            {1, -2}, {2, -1}, {2, 1}, {1, 2},
            {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}
    };

    /** How many more meals this swarm can eat before it flies away. */
    private int mealsLeft;

    /**
     * Creates a full-sized locust swarm.
     */
    public LocustSwarm() {
        this(STARTING_MEALS);
    }

    /**
     * Creates a locust swarm that can eat the given number of meals. Used
     * when a swarm splits.
     *
     * @param meals how many meals the swarm can eat
     * @throws IllegalArgumentException if meals is less than 1
     */
    private LocustSwarm(int meals) {
        super(NAME, DISPLAY_CHAR, HIT_POINTS, JUMP_OFFSETS);
        if (meals < 1) {
            throw new IllegalArgumentException("A swarm needs at least one meal, got " + meals);
        }
        this.mealsLeft = meals;
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
        if (mealsLeft < MEALS_NEEDED_TO_SPLIT || !RandomUtils.rollPercent(SPLIT_CHANCE_PERCENT)) {
            return "";
        }
        LocustSwarm offshoot = new LocustSwarm(mealsLeft / 2);
        Optional<Location> landing = Area.adjacentTo(here).randomEnterableTile(offshoot);
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
        for (Location tile : Area.centredOn(here).tiles()) {
            if (tile.getGroundAs(Regressible.class) != null) {
                plantTiles.add(tile);
            }
        }
        return plantTiles;
    }
}
