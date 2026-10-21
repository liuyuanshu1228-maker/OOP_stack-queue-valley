package game.runes;

/**
 * Capability interface for Ground that can grant a one-time boost to the
 * next rune formation triggered on it.
 * <p>
 * Only {@link Altar} implements this today, but code that wants to check for
 * a boost (see {@link PlaceRuneStoneAction}) depends on this contract rather
 * than on Altar directly, and can do so without instanceof via
 * {@code Location.getGroundAs(Boostable.class)}.
 */
public interface Boostable {

    /**
     * Consumes this ground's boost, if it still has one to give. Calling
     * this twice in a row returns true only once.
     *
     * @return true if a boost was available and has now been used, false if it was already spent
     */
    boolean consumeBoost();
}
