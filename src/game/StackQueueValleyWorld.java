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
import game.crops.Cauliflower;
import game.crops.CauliflowerSprout;
import game.crops.Melon;
import game.crops.MelonSprout;
import game.crops.Parsnip;
import game.crops.Strawberry;
import game.crops.StrawberrySeedling;
import game.crops.StrawberrySprout;
import game.crops.Tomato;
import game.crow.Crow;
import game.enchantments.BlazeEnchantment;
import game.enchantments.BountyEnchantment;
import game.enchantments.ShatterEnchantment;
import game.items.Sword;
import game.items.mine.Geode;
import game.items.tools.Enchantment;
import game.items.tools.Hoe;
import game.items.tools.Pickaxe;
import game.items.tools.WateringCan;
import game.pestcontrol.LadybugJar;
import game.pestcontrol.PestTrap;
import game.pestcontrol.SaltBarrier;
import game.pests.BurrowingMole;
import game.pests.LocustSwarm;
import game.pests.SlimeSnail;
import game.plants.Plant;
import game.positions.Plantable;
import game.positions.area.AreaFactory;
import game.positions.facilities.DryingRack;
import game.positions.facilities.PreservesJar;
import game.positions.farm.Dirt;
import game.positions.farm.Soil;
import game.positions.mine.CopperNode;
import game.positions.mine.Rock;
import game.random.RandomUtils;
import game.travel.AlienDevice;
import game.travel.Ladder;
import game.travel.TeleportationCircle;
import game.travel.TravelGround;
import game.wanderer.Wanderer;
import game.wanderer.WeatherInterpreter;
import game.weather.ApiKeyLoader;
import game.weather.OpenWeatherClient;
import game.weather.OpenWeatherParser;
import game.weather.MapGeography;
import game.weather.WeatherSource;

import java.nio.file.Path;
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

    /** The environment variable (or .env line) OpenWeatherClient reads its API key from. */
    private static final String OPENWEATHER_API_KEY_NAME = "OPENWEATHER_API_KEY";
    /** The latitude of the farm map's top-left tile (Melbourne). */
    private static final double FARM_ORIGIN_LATITUDE = -37.81;
    /** The longitude of the farm map's top-left tile (Melbourne). */
    private static final double FARM_ORIGIN_LONGITUDE = 144.96;
    /** How many degrees of latitude/longitude one farm tile covers. */
    private static final double FARM_DEGREES_PER_TILE = 2.0;

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

        // Dirt and Soil only; plantCrops then puts one of every plant on the Soil.
        List<String> inheritedFarm = Arrays.asList(
                "..,,,,,,..",
                ".,,,,,,,,.",
                ".,,,,,,,,.",
                ".,,,,.,,,.",
                ".,,,,,,,,.",
                ".,,,,,,,,.",
                ".........."
        );

        GameMap inheritedFarmMap = new GameMap("Inherited Farm", groundCreator, inheritedFarm);
        this.addGameMap(inheritedFarmMap);
        plantCrops(inheritedFarmMap, randomUtils, areaFactory);
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
                        new Parsnip(randomUtils, areaFactory),
                        new Tomato(randomUtils, areaFactory),
                        new Sword(randomUtils)),
                randomUtils);
        placeActor(inheritedFarmMap, occupiedLocations, new Crow("Crow", randomUtils, areaFactory), randomUtils);
        placeActor(inheritedFarmMap, occupiedLocations, new SlimeSnail(randomUtils, areaFactory), randomUtils);
        placeActor(inheritedFarmMap, occupiedLocations, new LocustSwarm(randomUtils, areaFactory), randomUtils);
        placeActor(inheritedFarmMap, occupiedLocations, new BurrowingMole(randomUtils, areaFactory), randomUtils);
        placeActor(inheritedFarmMap, occupiedLocations, buildWanderer(randomUtils, areaFactory), randomUtils);

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
     * Puts one of every plant in its fixed place on the farm. Each plant has
     * Soil around it to spread onto, and the Cauliflower is kept away from the
     * ladders and circles, as watering it may turn its neighbours into puddles.
     * <pre>
     *   x: 0123456789
     * y=1  .,',v,,S,.     ' Strawberry Seedling, v Strawberry Sprout, S Strawberry
     * y=3  .,t,,.,M,.     t Melon Sprout, M Melon
     * y=5  .,T,y,K,P.     T Tomato, y Cauliflower Sprout, K Cauliflower, P Parsnip
     * </pre>
     * The plants are built here, rather than from characters in the map text,
     * because each one needs the RandomUtils and AreaFactory passed to it.
     *
     * @param farm the farm map to plant on
     * @param randomUtils the randomness handed to every plant
     * @param areaFactory the AreaFactory handed to every plant
     */
    private void plantCrops(GameMap farm, RandomUtils randomUtils, AreaFactory areaFactory) {
        farm.at(2, 1).setGround(new StrawberrySeedling(randomUtils, areaFactory));
        farm.at(4, 1).setGround(new StrawberrySprout(randomUtils, areaFactory));
        farm.at(7, 1).setGround(new Strawberry(randomUtils, areaFactory));
        farm.at(2, 3).setGround(new MelonSprout(randomUtils, areaFactory));
        farm.at(7, 3).setGround(new Melon(randomUtils, areaFactory));
        farm.at(2, 5).setGround(new Tomato(randomUtils, areaFactory));
        farm.at(4, 5).setGround(new CauliflowerSprout(randomUtils, areaFactory));
        farm.at(6, 5).setGround(new Cauliflower(randomUtils, areaFactory));
        farm.at(8, 5).setGround(new Parsnip(randomUtils, areaFactory));
    }
    /**
     * Builds a Wanderer wired to the real OpenWeatherMap-backed WeatherSource
     * and to this farm's geography, so the weather it asks about is the real
     * weather at the tile it stands on.
     *
     * @param randomUtils the randomness used to build the Wanderer's fallback
     *                    wandering behaviour
     * @param areaFactory builds the Area each WeatherReaction it performs affects
     * @return a new Wanderer
     */
    private Wanderer buildWanderer(RandomUtils randomUtils, AreaFactory areaFactory) {
        ApiKeyLoader apiKeyLoader = new ApiKeyLoader(OPENWEATHER_API_KEY_NAME,
                System.getenv(), Path.of(".env"));
        WeatherSource weatherSource = new OpenWeatherClient(apiKeyLoader, new OpenWeatherParser());
        MapGeography mapGeography = new MapGeography(FARM_ORIGIN_LATITUDE, FARM_ORIGIN_LONGITUDE,
                FARM_DEGREES_PER_TILE);
        return new Wanderer(randomUtils, weatherSource, mapGeography, new WeatherInterpreter(areaFactory, randomUtils));
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
