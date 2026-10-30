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
import game.crops.suppliers.CauliflowerGroundSupplier;
import game.crops.suppliers.CauliflowerSproutGroundSupplier;
import game.crops.suppliers.MelonGroundSupplier;
import game.crops.suppliers.MelonSproutGroundSupplier;
import game.crops.suppliers.ParsnipGroundSupplier;
import game.crops.suppliers.StrawberryGroundSupplier;
import game.crops.suppliers.StrawberrySeedlingGroundSupplier;
import game.crops.suppliers.StrawberrySproutGroundSupplier;
import game.crops.suppliers.TomatoGroundSupplier;
import game.crow.Crow;
import game.travel.AlienDevice;
import game.enchantments.BlazeEnchantment;
import game.enchantments.BountyEnchantment;
import game.enchantments.Enchantment;
import game.enchantments.ShatterEnchantment;
import game.items.mine.Geode;
import game.items.tools.Hoe;
import game.items.tools.Pickaxe;
import game.items.tools.WateringCan;
import game.positions.area.AreaFactory;
import game.positions.mine.CopperNode;
import game.positions.farm.Dirt;
import game.positions.farm.Soil;
import game.plants.Plant;
import game.positions.Plantable;
import game.positions.facilities.DryingRack;
import game.travel.Ladder;
import game.positions.facilities.PreservesJar;
import game.positions.mine.Rock;
import game.random.RandomUtils;
import game.travel.TeleportationCircle;
import game.pestcontrol.LadybugJar;
import game.pestcontrol.PestTrap;
import game.pestcontrol.SaltBarrier;
import game.pests.BurrowingMole;
import game.pests.LocustSwarm;
import game.pests.SlimeSnail;
import game.travel.TravelGround;
import game.trading.ParsnipPurchase;
import game.trading.SwordPurchase;
import game.trading.TomatoPurchase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the game world of StackQueue Valley, responsible for initializing
 * the Inherited Farm and The Mines maps, linking travel grounds, and spawning
 * grounds, items, and actors (including the Farmer, Animals, and Merchant).
 * <p>
 * This class is the game's composition root: the one place that constructs
 * the shared {@link RandomUtils} and {@link AreaFactory} with {@code new} and
 * hands them down to every Ground, Item and Actor that needs one. Nothing
 * downstream ever reaches for its own global instance.
 */
public class StackQueueValleyWorld extends World {

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
        RandomUtils randomUtils = new RandomUtils();
        AreaFactory areaFactory = new AreaFactory(randomUtils);

        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround(',', Soil::new);
        groundCreator.registerGround('\'', new StrawberrySeedlingGroundSupplier(randomUtils, areaFactory));
        groundCreator.registerGround('v', new StrawberrySproutGroundSupplier(randomUtils, areaFactory));
        groundCreator.registerGround('y', new CauliflowerSproutGroundSupplier(randomUtils, areaFactory));
        groundCreator.registerGround('t', new MelonSproutGroundSupplier(randomUtils, areaFactory));
        groundCreator.registerGround('S', new StrawberryGroundSupplier(randomUtils, areaFactory));
        groundCreator.registerGround('K', new CauliflowerGroundSupplier(randomUtils, areaFactory));
        groundCreator.registerGround('M', new MelonGroundSupplier(randomUtils, areaFactory));
        groundCreator.registerGround('T', new TomatoGroundSupplier(randomUtils, areaFactory));
        groundCreator.registerGround('P', new ParsnipGroundSupplier(randomUtils, areaFactory));

        // One of every plant, each with Soil around it to spread onto. The
        // Cauliflower is kept away from the ladders and circles, as watering
        // it may turn its neighbours into puddles.
        List<String> inheritedFarm = Arrays.asList(
                "..,,,,,,..",
                ".,',v,,S,.",
                ".,,,,,,,,.",
                ".,t,,.,M,.",
                ".,,,,,,,,.",
                ".,T,y,K,P.",
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
        List<Enchantment> toolEnchantments =
                List.of(new BlazeEnchantment(), new BountyEnchantment(), new ShatterEnchantment());
        farmer.getInventory().add(new Hoe(toolEnchantments, areaFactory));
        farmer.getInventory().add(new WateringCan(toolEnchantments, areaFactory));
        farmer.getInventory().add(new Pickaxe(toolEnchantments, randomUtils, areaFactory));
        farmer.getInventory().add(new PestTrap(areaFactory));
        farmer.getInventory().add(new LadybugJar(randomUtils));
        farmer.getInventory().add(new SaltBarrier(areaFactory));

        Set<Location> occupiedLocations = new HashSet<>();
        occupiedLocations.add(farmerStart);
        reservePlantsAndSoil(inheritedFarmMap, occupiedLocations, areaFactory);
        Set<Location> minesOccupiedLocations = new HashSet<>();

        linkMaps(inheritedFarmMap, minesMap, occupiedLocations, minesOccupiedLocations,
                List.of(new Ladder(randomUtils), new Ladder(randomUtils)),
                List.of(new Ladder(randomUtils), new Ladder(randomUtils)),
                LADDER_LINKS, areaFactory);
        linkMaps(inheritedFarmMap, minesMap, occupiedLocations, minesOccupiedLocations,
                List.of(new TeleportationCircle(areaFactory), new TeleportationCircle(areaFactory)),
                List.of(new TeleportationCircle(areaFactory), new TeleportationCircle(areaFactory)),
                CIRCLE_LINKS, areaFactory);

        scatterGround(inheritedFarmMap, occupiedLocations, List.of(new DryingRack()), randomUtils);
        scatterGround(inheritedFarmMap, occupiedLocations, List.of(new PreservesJar()), randomUtils);

        placeActor(inheritedFarmMap, occupiedLocations, new Chicken("Chicken", randomUtils, areaFactory), randomUtils);
        placeActor(inheritedFarmMap, occupiedLocations, new Cow("Cow", randomUtils, areaFactory), randomUtils);
        placeActor(inheritedFarmMap, occupiedLocations,
                new Merchant(randomUtils,
                        new ParsnipPurchase(randomUtils, areaFactory),
                        new TomatoPurchase(randomUtils, areaFactory),
                        new SwordPurchase(randomUtils)),
                randomUtils);
        placeActor(inheritedFarmMap, occupiedLocations, new Crow("Crow", randomUtils, areaFactory), randomUtils);
        placeActor(inheritedFarmMap, occupiedLocations, new SlimeSnail(randomUtils, areaFactory), randomUtils);
        placeActor(inheritedFarmMap, occupiedLocations, new LocustSwarm(randomUtils, areaFactory), randomUtils);
        placeActor(inheritedFarmMap, occupiedLocations, new BurrowingMole(randomUtils, areaFactory), randomUtils);

        Location alienDeviceFarmDestination =
                locate(inheritedFarmMap, ALIEN_DEVICE_DESTINATIONS.farm());
        Location alienDeviceMinesDestination = locate(minesMap, ALIEN_DEVICE_DESTINATIONS.mines());
        AlienDevice alienDevice = new AlienDevice(randomUtils, areaFactory);
        alienDevice.addDestination(TO_FARM, alienDeviceFarmDestination);
        alienDevice.addDestination(TO_MINES, alienDeviceMinesDestination);
        minesOccupiedLocations.add(alienDeviceMinesDestination);
        scatterItems(inheritedFarmMap, occupiedLocations, List.of(alienDevice), randomUtils);

        scatterGround(minesMap, minesOccupiedLocations, buildRocks(randomUtils, areaFactory, ROCK_COUNT), randomUtils);
        scatterGround(minesMap, minesOccupiedLocations, buildCopperNodes(areaFactory, COPPER_NODE_COUNT), randomUtils);
        scatterItems(minesMap, minesOccupiedLocations, buildGeodes(randomUtils, areaFactory, GEODE_COUNT), randomUtils);
    }

    /**
     * Builds a list of brand-new Rocks, each wired with the given dependencies.
     * A fresh instance is built for every tile, exactly as a fresh instance
     * was built for every tile when this scattered a Supplier.
     *
     * @param randomUtils the randomness handed to every Rock built
     * @param areaFactory the AreaFactory handed to every Rock built
     * @param count how many Rocks to build
     * @return a list of {@code count} distinct new Rocks
     */
    private List<Ground> buildRocks(RandomUtils randomUtils, AreaFactory areaFactory, int count) {
        List<Ground> rocks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            rocks.add(new Rock(randomUtils, areaFactory));
        }
        return rocks;
    }

    /**
     * Builds a list of brand-new CopperNodes, each wired with the given AreaFactory.
     *
     * @param areaFactory the AreaFactory handed to every CopperNode built
     * @param count how many CopperNodes to build
     * @return a list of {@code count} distinct new CopperNodes
     */
    private List<Ground> buildCopperNodes(AreaFactory areaFactory, int count) {
        List<Ground> copperNodes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            copperNodes.add(new CopperNode(areaFactory));
        }
        return copperNodes;
    }

    /**
     * Builds a list of brand-new Geodes, each wired with the given dependencies.
     *
     * @param randomUtils the randomness handed to every Geode built
     * @param areaFactory the AreaFactory handed to every Geode built
     * @param count how many Geodes to build
     * @return a list of {@code count} distinct new Geodes
     */
    private List<Item> buildGeodes(RandomUtils randomUtils, AreaFactory areaFactory, int count) {
        List<Item> geodes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            geodes.add(new Geode(randomUtils, areaFactory));
        }
        return geodes;
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
     * @param farmSides a brand-new travel ground for the farm side of each link, in order
     * @param minesSides a brand-new travel ground for the mines side of each link, in order
     * @param links the positions of each pair
     * @param areaFactory builds the Area reserved around each placed ground
     */
    private void linkMaps(GameMap farmMap, GameMap minesMap, Set<Location> farmOccupiedLocations,
                          Set<Location> minesOccupiedLocations,
                          List<TravelGround> farmSides, List<TravelGround> minesSides,
                          List<Link> links, AreaFactory areaFactory) {
        for (int i = 0; i < links.size(); i++) {
            Link link = links.get(i);
            Location farmLocation = locate(farmMap, link.farm());
            Location minesLocation = locate(minesMap, link.mines());
            TravelGround farmSide = farmSides.get(i);
            TravelGround minesSide = minesSides.get(i);
            farmLocation.setGround(farmSide);
            minesLocation.setGround(minesSide);
            farmSide.addDestination(TO_MINES, minesLocation);
            minesSide.addDestination(TO_FARM, farmLocation);
            reserveWithNeighbours(farmOccupiedLocations, farmLocation, areaFactory);
            reserveWithNeighbours(minesOccupiedLocations, minesLocation, areaFactory);
        }
    }

    /**
     * Records every location of the map that holds a plant or Soil as used,
     * so that the grounds, items and actors placed at random afterwards
     * never cover a plant or the Soil it needs to spread onto.
     *
     * @param map the map whose plants and Soil to reserve
     * @param occupiedLocations the set of used locations to update
     * @param areaFactory builds the Area of the whole map
     */
    private void reservePlantsAndSoil(GameMap map, Set<Location> occupiedLocations, AreaFactory areaFactory) {
        for (Location location : areaFactory.wholeMap(map).tiles()) {
            if (location.getGroundAs(Plant.class) != null
                    || location.getGroundAs(Plantable.class) != null) {
                occupiedLocations.add(location);
            }
        }
    }

    /**
     * Records the given location, and every location adjacent to it, as used.
     *
     * @param occupiedLocations the set of used locations to update
     * @param location the location to reserve together with its neighbours
     * @param areaFactory builds the Area of the location and its neighbours
     */
    private void reserveWithNeighbours(Set<Location> occupiedLocations, Location location, AreaFactory areaFactory) {
        occupiedLocations.addAll(areaFactory.centredOn(location).tiles());
    }

    /**
     * Places each of the given Grounds on a random, distinct tile of the given
     * map, avoiding any location already recorded in {@code occupiedLocations}.
     * Every newly used location is added to {@code occupiedLocations}, so
     * repeated calls for different Grounds (or Actors, via {@link #placeActor})
     * never overlap.
     *
     * @param map the map to place Grounds on
     * @param occupiedLocations locations that must not be used; updated with the newly used ones
     * @param grounds the brand-new Ground instances to place, one tile each
     * @param randomUtils the randomness used to pick each tile
     */
    private void scatterGround(GameMap map, Set<Location> occupiedLocations, List<Ground> grounds,
                               RandomUtils randomUtils) {
        for (Ground ground : grounds) {
            claimRandomFreeLocation(map, occupiedLocations, randomUtils).setGround(ground);
        }
    }

    /**
     * Places each of the given Items on a random, distinct tile of the given
     * map, avoiding any location already recorded in {@code occupiedLocations}.
     * Every newly used location is added to {@code occupiedLocations}.
     *
     * @param map the map to place Items on
     * @param occupiedLocations locations that must not be used; updated with the newly used ones
     * @param items the Item instances to place, one tile each
     * @param randomUtils the randomness used to pick each tile
     */
    private void scatterItems(GameMap map, Set<Location> occupiedLocations, List<Item> items,
                              RandomUtils randomUtils) {
        for (Item item : items) {
            claimRandomFreeLocation(map, occupiedLocations, randomUtils).addItem(item);
        }
    }

    /**
     * Places a single Actor on a random tile of the given map, avoiding any
     * location already recorded in {@code occupiedLocations}.
     *
     * @param map the map to place the Actor on
     * @param occupiedLocations locations that must not be used; updated with the newly used one
     * @param actor the Actor to place
     * @param randomUtils the randomness used to pick the tile
     * @throws Exception if the engine cannot place the Actor on the chosen tile
     */
    private void placeActor(GameMap map, Set<Location> occupiedLocations, Actor actor, RandomUtils randomUtils)
            throws Exception {
        claimRandomFreeLocation(map, occupiedLocations, randomUtils).addActor(actor);
    }

    /**
     * Picks a random location on the map that is not already recorded in
     * {@code occupiedLocations}, and records it there.
     *
     * @param map the map to pick a location on
     * @param occupiedLocations locations that must not be used; updated with the chosen one
     * @param randomUtils the randomness used to pick the location
     * @return the chosen location
     */
    private Location claimRandomFreeLocation(GameMap map, Set<Location> occupiedLocations, RandomUtils randomUtils) {
        Location location;
        do {
            location = randomLocation(map, randomUtils);
        } while (occupiedLocations.contains(location));
        occupiedLocations.add(location);
        return location;
    }

    /**
     * @param map the map to pick a location on
     * @param randomUtils the randomness used to pick the location
     * @return a uniformly random location on the map
     */
    private Location randomLocation(GameMap map, RandomUtils randomUtils) {
        int x = randomUtils.randomInt(map.getXRange().max() + 1);
        int y = randomUtils.randomInt(map.getYRange().max() + 1);
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
