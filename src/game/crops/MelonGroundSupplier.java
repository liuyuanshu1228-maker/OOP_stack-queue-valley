package game.crops;

import edu.monash.fit2099.engine.positions.Ground;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

import java.util.function.Supplier;

/**
 * Builds a new {@link Melon} each time it is asked, carrying the RandomUtils
 * and AreaFactory the crop needs, so that
 * {@code DefaultGroundCreator.registerGround} (which requires a zero-argument
 * {@code Supplier<? extends Ground>}) can still build a Melon even though its
 * own constructor takes dependencies.
 */
public class MelonGroundSupplier implements Supplier<Ground> {

    private final RandomUtils randomUtils;
    private final AreaFactory areaFactory;

    /**
     * Creates a supplier that builds Melons with the given dependencies.
     *
     * @param randomUtils the randomness handed to every Melon built
     * @param areaFactory the AreaFactory handed to every Melon built
     */
    public MelonGroundSupplier(RandomUtils randomUtils, AreaFactory areaFactory) {
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * @return a newly constructed Melon
     */
    @Override
    public Ground get() {
        return new Melon(randomUtils, areaFactory);
    }
}
