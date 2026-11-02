package game.crow;

/**
 * Capability interface for a creature that has a sensing radius of its own.
 * Lets {@link CrowSensing} ask a creature how far it can sense without
 * depending on the creature being a {@link Crow} specifically.
 */
public interface SensingRange {

    /**
     * Returns how far this creature can sense.
     *
     * @return how far, in tiles, this creature can sense around itself
     */
    public int sensingRadius();
}
