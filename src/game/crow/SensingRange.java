package game.crow;

/**
 * Capability interface for a creature that has a sensing radius of its own.
 * Lets {@link CrowSensing} ask a creature how far it can sense without
 * depending on the creature being a {@link Crow} specifically.
 */
public interface SensingRange {

    /**
     * @return how far, in tiles, this creature can sense around itself
     */
    int sensingRadius();
}
