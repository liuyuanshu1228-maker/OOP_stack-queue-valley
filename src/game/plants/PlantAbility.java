package game.plants;

import edu.monash.fit2099.engine.positions.Location;

/**
 * One thing a {@link Plant} may do on its turn, such as growing into its next
 * stage, self-replicating, or producing an item.
 * <p>
 * A plant tries its abilities in priority order and stops at the first one
 * that actually does something, so every ability must report whether it
 * acted. An empty string means it did nothing this turn.
 */
public interface PlantAbility {

    /**
     * Tries this ability once for the given plant.
     *
     * @param plant the plant using this ability
     * @param location the location of the plant
     * @return a description of what happened, including coordinates, or an
     *         empty string if nothing happened
     */
    String tryPerform(Plant plant, Location location);

    /**
     * Tries this ability immediately, skipping whatever gating a subclass
     * normally applies before {@link #tryPerform} (such as an interval or a
     * chance roll). An ability with no such gating behaves exactly like
     * {@link #tryPerform}; {@link PeriodicAbility} overrides this to go
     * straight to its effect instead.
     *
     * @param plant the plant using this ability
     * @param location the location of the plant
     * @return a description of what happened, including coordinates, or an
     *         empty string if nothing happened
     */
    default String forcePerform(Plant plant, Location location) {
        return tryPerform(plant, location);
    }
}
