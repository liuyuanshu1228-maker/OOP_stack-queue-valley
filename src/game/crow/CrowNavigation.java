package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Utility methods for the Crow's movement and spatial reasoning: measuring
 * distance, searching for the nearest matching tile, and picking the best
 * adjacent step toward or away from a reference Location.
 * <p>
 * Kept separate from {@link game.positions.LocationUtils} because these
 * methods exist only to support the Crow's state-machine behaviours
 * ({@link Foraging}, {@link Mobbing}, {@link Fleeing}, {@link CrowSensing})
 * and change only for reasons related to the Crow's AI, never for reasons
 * related to general ground/item placement.
 */
final class CrowNavigation {

    private CrowNavigation() {
        // Prevent instantiation; this class only provides static utility methods.
    }

    /**
     * The distance between two Locations on the same map, measured as the
     * number of tile-steps a king would need on a chessboard (the same
     * metric {@link Location#getNearbyLocations} uses for its square radius).
     *
     * @param a the first Location
     * @param b the second Location
     * @return the Chebyshev distance between a and b
     */
    static int chebyshevDistance(Location a, Location b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }

    /**
     * Finds the closest Location to referenceLocation, within the given
     * radius, that satisfies the given condition.
     *
     * @param referenceLocation the Location to search outward from
     * @param radius the maximum distance to search
     * @param condition the condition a candidate Location must satisfy
     * @return the closest matching Location, or empty if none match
     */
    static Optional<Location> nearestWhere(Location referenceLocation, int radius, Predicate<Location> condition) {
        Location nearest = null;
        int nearestDistance = Integer.MAX_VALUE;

        for (Location candidate : referenceLocation.getNearbyLocations(radius)) {
            if (!condition.test(candidate)) {
                continue;
            }
            int distance = chebyshevDistance(referenceLocation, candidate);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = candidate;
            }
        }

        return Optional.ofNullable(nearest);
    }

    /**
     * Picks the tile adjacent to from (that actor could step onto) which
     * brings actor closest to target, by Chebyshev distance.
     *
     * @param from the Location to step from
     * @param target the Location being approached
     * @param actor the Actor that would be doing the stepping
     * @return the best adjacent Location to step to, or empty if no adjacent
     * tile is enterable or gets any closer than staying put
     */
    static Optional<Location> stepToward(Location from, Location target, Actor actor) {
        return bestStep(from, target, actor, StepDirection.TOWARD);
    }

    /**
     * Picks the tile adjacent to from (that actor could step onto) which
     * takes actor furthest away from threat, by Chebyshev distance.
     *
     * @param from the Location to step from
     * @param threat the Location being fled from
     * @param actor the Actor that would be doing the stepping
     * @return the best adjacent Location to step to, or empty if no adjacent
     * tile is enterable or gets any further away than staying put
     */
    static Optional<Location> stepAway(Location from, Location threat, Actor actor) {
        return bestStep(from, threat, actor, StepDirection.AWAY);
    }

    /**
     * Picks the enterable tile adjacent to from that most improves actor's
     * Chebyshev distance to reference in the given direction.
     *
     * @param from the Location to step from
     * @param reference the Location the distance is measured to
     * @param actor the Actor that would be doing the stepping
     * @param direction whether to approach (minimise the distance) or flee (maximise it)
     * @return the best adjacent Location to step to, or empty if no adjacent
     * tile is enterable or improves on staying put
     */
    private static Optional<Location> bestStep(Location from, Location reference, Actor actor,
                                               StepDirection direction) {
        Location best = null;
        int bestScore = direction.score(chebyshevDistance(from, reference));

        for (Location candidate : from.getNearbyLocations(1)) {
            if (!candidate.canActorEnter(actor)) {
                continue;
            }
            int score = direction.score(chebyshevDistance(candidate, reference));
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        return Optional.ofNullable(best);
    }

    /**
     * Which way a step should move an actor relative to a reference Location.
     */
    private enum StepDirection {
        /** Move closer to the reference Location. */
        TOWARD(-1),
        /** Move further away from the reference Location. */
        AWAY(1);

        private final int sign;

        /**
         * @param sign -1 if a smaller distance is better, +1 if a larger distance is better
         */
        StepDirection(int sign) {
            this.sign = sign;
        }

        /**
         * Converts a distance into a score where a higher score is always a better step.
         *
         * @param distance the Chebyshev distance to the reference Location
         * @return the score of that distance for this direction
         */
        int score(int distance) {
            return sign * distance;
        }
    }
}
