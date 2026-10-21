package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actors.Chicken;
import game.actors.Cow;
import game.actors.Farmer;
import game.actors.Merchant;
import game.crow.Crow;
import game.travel.AlienDevice;
import game.items.mine.Geode;
import game.items.tools.Hoe;
import game.items.tools.Pickaxe;
import game.positions.area.Area;
import game.items.tools.WateringCan;
import game.crops.Cauliflower;
import game.positions.mine.CopperNode;
import game.positions.farm.Dirt;
import game.positions.facilities.DryingRack;
import game.runes.EmberRune;
import game.runes.QuakeRune;
import game.runes.RuneStoneItem;
import game.travel.Ladder;
import game.crops.Melon;
import game.crops.Parsnip;
import game.positions.facilities.PreservesJar;
import game.positions.mine.Rock;
import game.random.RandomUtils;
import game.crops.Strawberry;
import game.travel.TeleportationCircle;
import game.crops.Tomato;
import game.travel.TravelGround;
import game.trading.ParsnipPurchase;
import game.trading.SwordPurchase;
import game.trading.TomatoPurchase;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Represents the game world of StackQueue Valley, responsible for initializing
 * the Inherited Farm and The Mines maps, linking travel grounds, and spawning
 * grounds, items, and actors (including the Farmer, Animals, and Merchant).
 *
 */
public class StackQueueValleyWorld extends World {

    /** Number of Parsnip crops scattered on the farm at the start of the game. */
    private static final int PARSNIP_COUNT = 5;
    /** Number of Strawberry crops scattered on the farm at the start of the game. */
    private static final int STRAWBERRY_COUNT = 5;
    /** Number of Cauliflower crops scattered on the farm at the start of the game. */
    private static final int CAULIFLOWER_COUNT = 5;
    /** Number of Melon crops scattered on the farm at the start of the game. */
    private static final int MELON_COUNT = 5;
    /** Number of Tomato crops scattered on the farm at the start of the game. */
    private static final int TOMATO_COUNT = 5;

    /** The name of the mines map. */
    private static final String MINES_MAP_NAME = "The Mines";
    /** The width, in tiles, of the mines map. */
    private static final int MINES_WIDTH = 12;
    /** The height, in tiles, of the mines map. */
    private static final int MINES_HEIGHT = 8;
    /** Number of rocks scattered in the mines at the start of the game. */
    private static final int ROCK_COUNT = 8;
    /** Number of copper nodes scattered in the mines at the start of the game. */
    private static final int COPPER_NODE_COUNT = 4;
    /** Number of geodes scattered in the mines at the start of the game. */
    private static final int GEODE_COUNT = 3;

    /** How many rune stones of each type the Farmer starts with. */
    private static final int RUNE_STONES_PER_TYPE = 4;

    /** Destination names shown in the travel menus. */
    private static final String TO_FARM = "the farm";
    /** Destination name of the mines, shown in the travel menus. */
    private static final String TO_MINES = "the mines";

    /**
     * A fixed position on a map.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    private record Spot(int x, int y) {
    }

    /**
     * A two-way link between a position on the farm and a position in the mines.
     *
     * @param farm the position on the farm
     * @param mines the position in the mines
     */
    private record Link(Spot farm, Spot mines) {
    }

    /** Each farm ladder and the mines ladder it is paired with. */
    private static final List<Link> LADDER_LINKS = List.of(
            new Link(new Spot(0, 0), new Spot(0, 0)),
            new Link(new Spot(9, 6), new Spot(11, 7)));

    /** Each farm teleportation circle and the mines circle it is paired with. */
    private static final List<Link> CIRCLE_LINKS = List.of(
            new Link(new Spot(9, 0), new Spot(11, 0)),
            new Link(new Spot(0, 6), new Spot(0, 7)));

    /** Where the Farmer starts on the farm. */
    private static final Spot FARMER_START = new Spot(5, 3);

    /** The fixed destinations of the alien device, on the farm and in the mines. */
    private static final Link ALIEN_DEVICE_DESTINATIONS = new Link(new Spot(5, 3), new Spot(6, 4));

    /**
     * Creates a new StackQueueValleyWorld.
     *
     * @param display the Display that will display this World
     */
    public StackQueueValleyWorld(Display display) {
        super(display);
    }

    /**
     * Initializes the game world by creating maps, registering grounds, placing the player,
     * linking travel grounds, and scattering initial crops, stations, items, and actors.
     *
     * @throws Exception if an error occurs while placing actors or initializing the world
     */
    public void initialise() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);

        List<String> inheritedFarm = Arrays.asList(
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                ".........."
        );

        GameMap inheritedFarmMap = new GameMap("Inherited Farm", groundCreator, inheritedFarm);
        this.addGameMap(inheritedFarmMap);
        GameMap minesMap = new GameMap(MINES_MAP_NAME, groundCreator, '.',
                MINES_WIDTH, MINES_HEIGHT);
        this.addGameMap(minesMap);

        Location farmerStart = locate(inheritedFarmMap, FARMER_START);
        Farmer farmer = new Farmer("Bob");
        this.addPlayer(farmer, farmerStart);
        farmer.getInventory().add(new Hoe());
        farmer.getInventory().add(new WateringCan());
        farmer.getInventory().add(new Pickaxe());
        giveRuneStones(farmer);

        Set<Location> occupiedLocations = new HashSet<>();
        occupiedLocations.add(farmerStart);
        Set<Location> minesOccupiedLocations = new HashSet<>();

        linkMaps(inheritedFarmMap, minesMap, occupiedLocations, minesOccupiedLocations,
                Ladder::new, LADDER_LINKS);
        linkMaps(inheritedFarmMap, minesMap, occupiedLocations, minesOccupiedLocations,
                TeleportationCircle::new, CIRCLE_LINKS);

        scatterGround(inheritedFarmMap, occupiedLocations, PARSNIP_COUNT, Parsnip::new);
        scatterGround(inheritedFarmMap, occupiedLocations, STRAWBERRY_COUNT, Strawberry::new);
        scatterGround(inheritedFarmMap, occupiedLocations, CAULIFLOWER_COUNT, Cauliflower::new);
        scatterGround(inheritedFarmMap, occupiedLocations, MELON_COUNT, Melon::new);
        scatterGround(inheritedFarmMap, occupiedLocations, TOMATO_COUNT, Tomato::new);
        scatterGround(inheritedFarmMap, occupiedLocations, 1, DryingRack::new);
        scatterGround(inheritedFarmMap, occupiedLocations, 1, PreservesJar::new);

        placeActor(inheritedFarmMap, occupiedLocations, new Chicken("Chicken"));
        placeActor(inheritedFarmMap, occupiedLocations, new Cow("Cow"));
        placeActor(inheritedFarmMap, occupiedLocations,
                new Merchant(new ParsnipPurchase(), new TomatoPurchase(), new SwordPurchase()));
        placeActor(inheritedFarmMap, occupiedLocations, new Crow("Crow"));

        Location alienDeviceFarmDestination =
                locate(inheritedFarmMap, ALIEN_DEVICE_DESTINATIONS.farm());
        Location alienDeviceMinesDestination = locate(minesMap, ALIEN_DEVICE_DESTINATIONS.mines());
        AlienDevice alienDevice = new AlienDevice();
        alienDevice.addDestination(TO_FARM, alienDeviceFarmDestination);
        alienDevice.addDestination(TO_MINES, alienDeviceMinesDestination);
        minesOccupiedLocations.add(alienDeviceMinesDestination);
        scatterItems(inheritedFarmMap, occupiedLocations, 1, () -> alienDevice);

        scatterGround(minesMap, minesOccupiedLocations, ROCK_COUNT, Rock::new);
        scatterGround(minesMap, minesOccupiedLocations, COPPER_NODE_COUNT, CopperNode::new);
        scatterItems(minesMap, minesOccupiedLocations, GEODE_COUNT, Geode::new);
    }

    /**
     * Gives the Farmer a starting supply of each working rune stone type
     * (Ember, Quake), enough of each to complete any of the three known rune
     * formations on their own.
     * <p>
     * Verdant Rune Stones are withheld for now: {@code VerdantRune} has no
     * real effect yet (it is blocked on REQ1's Plant/Seedling classes -- see
     * its javadoc), so handing the player one would just be a dead item in
     * their inventory. Add it back here once VerdantRune is finished.
     *
     * @param farmer the Farmer to give rune stones to
     */
    private void giveRuneStones(Farmer farmer) {
        for (int i = 0; i < RUNE_STONES_PER_TYPE; i++) {
            farmer.getInventory().add(new RuneStoneItem("Ember Rune Stone", 'e', new EmberRune()));
            farmer.getInventory().add(new RuneStoneItem("Quake Rune Stone", 'u', new QuakeRune()));
        }
    }

    /**
     * Places a pair of two-way travel grounds (such as ladders) for each link:
     * one on the farm and one in the mines, each leading to the other. The
     * tiles around every placed ground are reserved as well, so that nothing
     * placed at random afterwards can sit next to it. (A Cauliflower turns
     * every adjacent non-crop ground into a puddle, which would otherwise wipe
     * out a nearby ladder or circle; in the mines it also keeps rocks from
     * walling in an arrival point.)
     *
     * @param farmMap the farm map
     * @param minesMap the mines map
     * @param farmOccupiedLocations farm locations that must not be used; updated with
     *                              the newly used ones
     * @param minesOccupiedLocations mines locations that must not be used; updated with
     *                               the newly used ones
     * @param groundFactory a supplier that creates a new instance of the travel ground to place
     * @param links the positions of each pair
     */
    private void linkMaps(GameMap farmMap, GameMap minesMap, Set<Location> farmOccupiedLocations,
                          Set<Location> minesOccupiedLocations,
                          Supplier<? extends TravelGround> groundFactory, List<Link> links) {
        for (Link link : links) {
            Location farmLocation = locate(farmMap, link.farm());
            Location minesLocation = locate(minesMap, link.mines());
            TravelGround farmSide = groundFactory.get();
            TravelGround minesSide = groundFactory.get();
            farmLocation.setGround(farmSide);
            minesLocation.setGround(minesSide);
            farmSide.addDestination(TO_MINES, minesLocation);
            minesSide.addDestination(TO_FARM, farmLocation);
            reserveWithNeighbours(farmOccupiedLocations, farmLocation);
            reserveWithNeighbours(minesOccupiedLocations, minesLocation);
        }
    }

    /**
     * Records the given location, and every location adjacent to it, as used.
     *
     * @param occupiedLocations the set of used locations to update
     * @param location the location to reserve together with its neighbours
     */
    private void reserveWithNeighbours(Set<Location> occupiedLocations, Location location) {
        occupiedLocations.addAll(Area.centredOn(location).tiles());
    }

    /**
     * Places a number of Grounds on random, distinct tiles of the given map,
     * avoiding any location already recorded in {@code occupiedLocations}.
     * Every newly used location is added to {@code occupiedLocations}, so
     * repeated calls for different Grounds (or Actors, via {@link #placeActor})
     * never overlap.
     *
     * @param map the map to place Grounds on
     * @param occupiedLocations locations that must not be used; updated with the newly used ones
     * @param count how many Grounds to place
     * @param groundFactory a supplier that creates a new instance of the Ground to place
     */
    private void scatterGround(GameMap map, Set<Location> occupiedLocations, int count,
                               Supplier<? extends Ground> groundFactory) {
        for (int placed = 0; placed < count; placed++) {
            claimRandomFreeLocation(map, occupiedLocations).setGround(groundFactory.get());
        }
    }

    /**
     * Places a number of Items on random, distinct tiles of the given map,
     * avoiding any location already recorded in {@code occupiedLocations}.
     * Every newly used location is added to {@code occupiedLocations}.
     *
     * @param map the map to place Items on
     * @param occupiedLocations locations that must not be used; updated with the newly used ones
     * @param count how many Items to place
     * @param itemFactory a supplier that creates a new instance of the Item to place
     */
    private void scatterItems(GameMap map, Set<Location> occupiedLocations, int count,
                              Supplier<? extends Item> itemFactory) {
        for (int placed = 0; placed < count; placed++) {
            claimRandomFreeLocation(map, occupiedLocations).addItem(itemFactory.get());
        }
    }

    /**
     * Places a single Actor on a random tile of the given map, avoiding any
     * location already recorded in {@code occupiedLocations}.
     *
     * @param map the map to place the Actor on
     * @param occupiedLocations locations that must not be used; updated with the newly used one
     * @param actor the Actor to place
     */
    private void placeActor(GameMap map, Set<Location> occupiedLocations, Actor actor)
            throws Exception {
        claimRandomFreeLocation(map, occupiedLocations).addActor(actor);
    }

    /**
     * Picks a random location on the map that is not already recorded in
     * {@code occupiedLocations}, and records it there.
     *
     * @param map the map to pick a location on
     * @param occupiedLocations locations that must not be used; updated with the chosen one
     * @return the chosen location
     */
    private Location claimRandomFreeLocation(GameMap map, Set<Location> occupiedLocations) {
        Location location;
        do {
            location = randomLocation(map);
        } while (occupiedLocations.contains(location));
        occupiedLocations.add(location);
        return location;
    }

    /**
     * @param map the map to pick a location on
     * @return a uniformly random location on the map
     */
    private Location randomLocation(GameMap map) {
        int x = RandomUtils.randomInt(map.getXRange().max() + 1);
        int y = RandomUtils.randomInt(map.getYRange().max() + 1);
        return map.at(x, y);
    }

    /**
     * @param map the map the position is on
     * @param spot the position
     * @return the location at that position on the map
     */
    private Location locate(GameMap map, Spot spot) {
        return map.at(spot.x(), spot.y());
    }
}
