package game.crops.suppliers;

import edu.monash.fit2099.engine.positions.Ground;
import game.crops.Tomato;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

import java.util.function.Supplier;

/**
 * Builds a new {@link Tomato} each time it is asked, carrying the
 * RandomUtils and AreaFactory the crop needs, so that
 * {@code DefaultGroundCreator.registerGround} (which requires a zero-argument
 * {@code Supplier<? extends Ground>}) can still build a Tomato even though
 * its own constructor takes dependencies.
 */
public class TomatoGroundSupplier implements Supplier<Ground> {

    private final RandomUtils randomUtils;
    private final AreaFactory areaFactory;

    /**
     * Creates a supplier that builds Tomatoes with the given dependencies.
     *
     * @param randomUtils the randomness handed to every Tomato built
     * @param areaFactory the AreaFactory handed to every Tomato built
     */
    public TomatoGroundSupplier(RandomUtils randomUtils, AreaFactory areaFactory) {
        this.randomUtils = randomUtils;
        this.areaFactory = areaFactory;
    }

    /**
     * @return a newly constructed Tomato
     */
    @Override
    public Ground get() {
        return new Tomato(randomUtils, areaFactory);
    }
}
