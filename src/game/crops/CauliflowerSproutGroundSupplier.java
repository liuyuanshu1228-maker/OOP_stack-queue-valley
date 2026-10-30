package game.crops;

import edu.monash.fit2099.engine.positions.Ground;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

import java.util.function.Supplier;

/**
 * Builds a new {@link CauliflowerSprout} each time it is asked, carrying the
 * RandomUtils and AreaFactory needed by it and the Cauliflower it may grow
 * into, so that {@code DefaultGroundCreator.registerGround} (which requires a
 * zero-argument {@code Supplier<? extends Ground>}) can still build a
 * CauliflowerSprout even though its own constructor takes dependencies.
 */
public class CauliflowerSproutGroundSupplier implements Supplier<Ground> {

    private final RandomUtils randomUtils;
    private final AreaFactory areaFactory;

    /**
     * Creates a supplier that builds CauliflowerSprouts with the given dependencies.
     *
     * @param randomUtils the randomness handed to every CauliflowerSprout built
     * @param areaFactory the AreaFactory handed to every CauliflowerSprout built
     */
    public CauliflowerSproutGroundSupplier(RandomUtils randomUtils, AreaFactory areaFactory) {
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * @return a newly constructed CauliflowerSprout
     */
    @Override
    public Ground get() {
        return new CauliflowerSprout(randomUtils, areaFactory);
    }
}
