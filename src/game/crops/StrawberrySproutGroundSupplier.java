package game.crops;

import edu.monash.fit2099.engine.positions.Ground;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

import java.util.function.Supplier;

/**
 * Builds a new {@link StrawberrySprout} each time it is asked, carrying the
 * RandomUtils and AreaFactory every strawberry growth stage needs, so that
 * {@code DefaultGroundCreator.registerGround} (which requires a zero-argument
 * {@code Supplier<? extends Ground>}) can still build a StrawberrySprout even
 * though its own constructor takes dependencies.
 */
public class StrawberrySproutGroundSupplier implements Supplier<Ground> {

    private final RandomUtils randomUtils;
    private final AreaFactory areaFactory;

    /**
     * Creates a supplier that builds StrawberrySprouts with the given dependencies.
     *
     * @param randomUtils the randomness handed to every StrawberrySprout built
     * @param areaFactory the AreaFactory handed to every StrawberrySprout built
     */
    public StrawberrySproutGroundSupplier(RandomUtils randomUtils, AreaFactory areaFactory) {
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * @return a newly constructed StrawberrySprout
     */
    @Override
    public Ground get() {
        return new StrawberrySprout(randomUtils, areaFactory);
    }
}
