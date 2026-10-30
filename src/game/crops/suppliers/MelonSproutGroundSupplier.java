package game.crops.suppliers;

import edu.monash.fit2099.engine.positions.Ground;
import game.crops.MelonSprout;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

import java.util.function.Supplier;

/**
 * Builds a new {@link MelonSprout} each time it is asked, carrying the
 * RandomUtils and AreaFactory every melon growth stage needs, so that
 * {@code DefaultGroundCreator.registerGround} (which requires a zero-argument
 * {@code Supplier<? extends Ground>}) can still build a MelonSprout even
 * though its own constructor takes dependencies.
 */
public class MelonSproutGroundSupplier implements Supplier<Ground> {

    /** The randomness handed to every MelonSprout this supplier builds. */
    private final RandomUtils randomUtils;

    /** The AreaFactory handed to every MelonSprout this supplier builds. */
    private final AreaFactory areaFactory;

    /**
     * Creates a supplier that builds MelonSprouts with the given dependencies.
     *
     * @param randomUtils the randomness handed to every MelonSprout built
     * @param areaFactory the AreaFactory handed to every MelonSprout built
     */
    public MelonSproutGroundSupplier(RandomUtils randomUtils, AreaFactory areaFactory) {
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * @return a newly constructed MelonSprout
     */
    @Override
    public Ground get() {
        return new MelonSprout(randomUtils, areaFactory);
    }
}
