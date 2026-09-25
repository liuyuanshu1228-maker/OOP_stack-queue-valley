package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.cauliflower.Cauliflower;
import game.items.Hoe;
import game.items.WateringCan;
import game.melon.Melon;
import game.parsnip.Parsnip;
import game.strawberry.Strawberry;
import game.tomato.Tomato;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public class StackQueueValleyWorld extends World {

    private static final int PARSNIP_COUNT = 5;
    private static final int STRAWBERRY_COUNT = 5;
    private static final int CAULIFLOWER_COUNT = 5;
    private static final int MELON_COUNT = 5;
    private static final int TOMATO_COUNT = 5;

    private final Random random = new Random();

    /**
     * Constructor.
     *
     * @param display the Display that will display this World.
     */
    public StackQueueValleyWorld(Display display) {
        super(display);
    }

    public void initialise() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('P', Parsnip::new);
        groundCreator.registerGround('S', Strawberry::new);
        groundCreator.registerGround('C', Cauliflower::new);
        groundCreator.registerGround('M', Melon::new);
        groundCreator.registerGround('T', Tomato::new);
        groundCreator.registerGround('R', DryingRack::new);
        groundCreator.registerGround('J', PreservesJar::new);

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

        Location farmerStart = inheritedFarmMap.at(5, 3);
        Farmer farmer = new Farmer("Bob");
        this.addPlayer(farmer, farmerStart);
        farmer.getInventory().add(new Hoe());
        farmer.getInventory().add(new WateringCan());

        Set<Location> occupiedLocations = new HashSet<>();
        occupiedLocations.add(farmerStart);
        scatterGround(inheritedFarmMap, occupiedLocations, PARSNIP_COUNT, Parsnip::new);
        scatterGround(inheritedFarmMap, occupiedLocations, STRAWBERRY_COUNT, Strawberry::new);
        scatterGround(inheritedFarmMap, occupiedLocations, CAULIFLOWER_COUNT, Cauliflower::new);
        scatterGround(inheritedFarmMap, occupiedLocations, MELON_COUNT, Melon::new);
        scatterGround(inheritedFarmMap, occupiedLocations, TOMATO_COUNT, Tomato::new);
        scatterGround(inheritedFarmMap, occupiedLocations, 1, DryingRack::new);
        scatterGround(inheritedFarmMap, occupiedLocations, 1, PreservesJar::new);

        placeActor(inheritedFarmMap, occupiedLocations, new Chicken("Chicken"));
        placeActor(inheritedFarmMap, occupiedLocations, new Cow("Cow"));
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
    private void scatterGround(GameMap map, Set<Location> occupiedLocations, int count, Supplier<? extends Ground> groundFactory) {
        for (int placed = 0; placed < count; placed++) {
            claimRandomFreeLocation(map, occupiedLocations).setGround(groundFactory.get());
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
    private void placeActor(GameMap map, Set<Location> occupiedLocations, Actor actor) throws Exception {
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
        int x = random.nextInt(map.getXRange().max() + 1);
        int y = random.nextInt(map.getYRange().max() + 1);
        return map.at(x, y);
    }
}