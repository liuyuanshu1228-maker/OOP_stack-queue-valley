package game.runes;

import java.util.List;

/**
 * The full set of known rune formation shapes.
 * <p>
 * Pulled out of {@link PlaceRuneStoneAction} so that adding, removing or
 * reordering a RunePattern only ever touches this one place -- the Action
 * that orchestrates placing a stone and reverting it shouldn't also need to
 * change just because a new shape was invented (SRP). It is a plain registry
 * rather than a literal factory: every RunePattern implementation here is
 * stateless (none keeps mutable fields -- see each pattern's own javadoc on
 * why its matching logic is recomputed instead of cached), so handing out the
 * same shared instances is just as correct as minting a new one per call,
 * without the needless allocation (YAGNI).
 * <p>
 * Package-private: only {@link PlaceRuneStoneAction} needs this list.
 */
final class KnownRunePatterns {

    private static final List<RunePattern> ALL =
            List.of(new LinePattern(), new TrianglePattern(), new SquarePattern());

    private KnownRunePatterns() {
        // Utility class; not instantiable.
    }

    /**
     * @return every known rune formation shape, in the order they should be checked
     */
    static List<RunePattern> all() {
        return ALL;
    }
}
