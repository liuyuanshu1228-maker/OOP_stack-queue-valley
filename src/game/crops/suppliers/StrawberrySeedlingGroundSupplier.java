package game.crops.suppliers;

import edu.monash.fit2099.engine.positions.Ground;
import game.crops.StrawberrySeedling;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

import java.util.function.Supplier;

/**
 * Builds a new {@link StrawberrySeedling} each time it is asked, carrying the
 * RandomUtils and AreaFactory every strawberry growth stage needs, so that
 * {@code DefaultGroundCreator.registerGround} (which requires a zero-argument
 * {@code Supplier<? extends Ground>}) can still build a StrawberrySeedling
 * even though its own constructor takes dependencies.
 */
public class StrawberrySeedlingGroundSupplier implements Supplier<Ground> {

    private final RandomUtils randomUtils;
    private final AreaFactory areaFactory;

    /**
     * Creates a supplier that builds StrawberrySeedlings with the given dependencies.
     *
     * @param randomUtils the randomness handed to every StrawberrySeedling built
     * @param areaFactory the AreaFactory handed to every StrawberrySeedling built
     */
    public StrawberrySeedlingGroundSupplier(RandomUtils randomUtils, AreaFactory areaFactory) {
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * @return a newly constructed StrawberrySeedling
     */
    @Override
    public Ground get() {
        return new StrawberrySeedling(randomUtils, areaFactory);
    }
}
