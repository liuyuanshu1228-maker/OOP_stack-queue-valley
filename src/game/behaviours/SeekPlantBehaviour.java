package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.plants.Regressible;
import game.positions.area.Area;
import game.positions.area.AreaFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The behaviour of a pest that heads for plants: each turn it lands on the
 * tile that brings it closest to the nearest plant it can sense.
 * <p>
 * Where it can land is given as a set of offsets, so each kind of pest moves
 * in its own way (a crawl of one tile, a knight-like jump, a straight run)
 * without a new behaviour class. A pest can pass over tiles it cannot enter;
 * only the landing tile must be enterable. If no plant is in range it lands on
 * a random tile, and if it cannot land anywhere it uses its fallback
 * behaviour.
 */
public class SeekPlantBehaviour implements Behaviour<Actor, Action> {

    /** How far away, in tiles, a pest can sense a plant. */
    private static final int SENSING_RADIUS = 4;

    /** Each landing tile as {dx, dy} from the pest's tile. */
    private final int[][] landingOffsets;

    /** What to do when no landing tile can be entered. */
    private final Behaviour<Actor, Action> fallback;

    /** Builds the Areas used to find a landing tile and the nearest plant. */
    private final AreaFactory areaFactory;

    /**
     * Creates the behaviour.
     *
     * @param landingOffsets the landing tiles as {dx, dy} pairs; copied, so
     *                       later changes to the array have no effect
     * @param fallback what to do when no landing tile can be entered
     * @param areaFactory builds the Areas used to find a landing tile and the nearest plant
     * @throws IllegalArgumentException if there are no offsets or an offset
     *                                  is not a {dx, dy} pair
     */
    public SeekPlantBehaviour(int[][] landingOffsets, Behaviour<Actor, Action> fallback, AreaFactory areaFactory) {
        this.landingOffsets = copyOf(landingOffsets);
        this.fallback = fallback;
        this.areaFactory = areaFactory;
    }

    /**
     * Chooses where the pest lands this turn.
     *
     * @param actor the pest
     * @param location the tile the pest stands on
     * @return a move to the chosen landing tile, or the fallback's action
     */
    @Override
    public Action operate(Actor actor, Location location) {
        Area landings = areaFactory.of(enterableLandings(actor, location));
        Optional<Location> plant = nearestPlant(location);
        Optional<Location> landing;
        if (plant.isPresent()) {
            landing = landings.nearestTo(plant.get());
        } else {
            landing = landings.randomTile();
        }
        if (landing.isEmpty()) {
            return fallback.operate(actor, location);
        }
        Location target = landing.get();
        return new MoveActorAction(target, "to " + target);
    }

    /**
     * @param actor the pest
     * @param from the tile the pest stands on
     * @return the landing tiles that are on the map and that the pest can enter
     */
    private List<Location> enterableLandings(Actor actor, Location from) {
        GameMap map = from.map();
        List<Location> landings = new ArrayList<>();
        for (int[] offset : landingOffsets) {
            int x = from.x() + offset[0];
            int y = from.y() + offset[1];
            if (map.getXRange().contains(x) && map.getYRange().contains(y)) {
                Location tile = map.at(x, y);
                if (tile.canActorEnter(actor)) {
                    landings.add(tile);
                }
            }
        }
        return landings;
    }

    /**
     * @param from the tile the pest stands on
     * @return the nearest tile with a plant within sensing range, if any
     */
    private Optional<Location> nearestPlant(Location from) {
        List<Location> plants = new ArrayList<>();
        for (Location tile : areaFactory.within(from, SENSING_RADIUS).tiles()) {
            if (tile.getGroundAs(Regressible.class) != null) {
                plants.add(tile);
            }
        }
        return areaFactory.of(plants).nearestTo(from);
    }

    /**
     * Copies the offsets, so this behaviour cannot be changed from outside.
     *
     * @param offsets the offsets to copy
     * @return a deep copy of the offsets
     * @throws IllegalArgumentException if there are no offsets or an offset
     *                                  is not a {dx, dy} pair
     */
    private int[][] copyOf(int[][] offsets) {
        if (offsets == null || offsets.length == 0) {
            throw new IllegalArgumentException("a pest needs at least one landing offset");
        }
        int[][] copy = new int[offsets.length][];
        for (int i = 0; i < offsets.length; i++) {
            if (offsets[i] == null || offsets[i].length != 2) {
                throw new IllegalArgumentException("each landing offset must be a {dx, dy} pair");
            }
            copy[i] = new int[] {offsets[i][0], offsets[i][1]};
        }
        return copy;
    }
}
