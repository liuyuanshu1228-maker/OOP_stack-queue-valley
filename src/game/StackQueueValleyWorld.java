package game;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.parsnip.Parsnip;
import game.strawberry.Strawberry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public class StackQueueValleyWorld extends World {

    private static final int PARSNIP_COUNT = 5;
    private static final int STRAWBERRY_COUNT = 5;

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
        groundCreator.registerGround('p', Parsnip::new);
        groundCreator.registerGround('S', Strawberry::new);

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
        farmer.getInventory().add(new WateringCan());

        Set<Location> occupiedLocations = new HashSet<>();
        occupiedLocations.add(farmerStart);
        scatterCrop(inheritedFarmMap, occupiedLocations, PARSNIP_COUNT, Parsnip::new);
        scatterCrop(inheritedFarmMap, occupiedLocations, STRAWBERRY_COUNT, Strawberry::new);
    }

    /**
     * Plants a number of crops on random, distinct tiles of the given map,
     * avoiding any location already recorded in {@code occupiedLocations}.
     * Every newly planted location is added to {@code occupiedLocations}, so
     * repeated calls for different crops never overlap.
     *
     * @param map the map to scatter crops on
     * @param occupiedLocations locations that must not be planted on; updated with the newly planted ones
     * @param count how many crops to plant
     * @param cropFactory a supplier that creates a new instance of the crop to plant
     */
    private void scatterCrop(GameMap map, Set<Location> occupiedLocations, int count, Supplier<Crop> cropFactory) {
        Random random = new Random();
        int planted = 0;

        while (planted < count) {
            int x = random.nextInt(map.getXRange().max() + 1);
            int y = random.nextInt(map.getYRange().max() + 1);
            Location location = map.at(x, y);

            if (occupiedLocations.contains(location)) {
                continue;
            }

            location.setGround(cropFactory.get());
            occupiedLocations.add(location);
            planted++;
        }
    }
}
