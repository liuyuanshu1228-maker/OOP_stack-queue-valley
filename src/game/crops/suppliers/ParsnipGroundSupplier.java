package game.crops.suppliers;

import edu.monash.fit2099.engine.positions.Ground;
import game.crops.Parsnip;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

import java.util.function.Supplier;

/**
 * Builds a new {@link Parsnip} each time it is asked, carrying the
 * RandomUtils and AreaFactory the crop needs, so that
 * {@code DefaultGroundCreator.registerGround} (which requires a zero-argument
 * {@code Supplier<? extends Ground>}) can still build a Parsnip even though
 * its own constructor takes dependencies.
 */
public class ParsnipGroundSupplier implements Supplier<Ground> {

    private final RandomUtils randomUtils;
    private final AreaFactory areaFactory;

    /**
     * Creates a supplier that builds Parsnips with the given dependencies.
     *
     * @param randomUtils the randomness handed to every Parsnip built
     * @param areaFactory the AreaFactory handed to every Parsnip built
     */
    public ParsnipGroundSupplier(RandomUtils randomUtils, AreaFactory areaFactory) {
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * @return a newly constructed Parsnip
     */
    @Override
    public Ground get() {
        return new Parsnip(randomUtils, areaFactory);
    }
}
