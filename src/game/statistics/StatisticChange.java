package game.statistics;

/**
 * The result of changing one statistic: its value before and after the change.
 * <p>
 * {@link StatisticAdjuster} only performs the arithmetic and returns one of these;
 * how the change is shown to the player is decided here, so the two concerns can
 * change independently.
 */
public class StatisticChange {

    /** The statistic's value before the change. */
    private final int before;

    /** The statistic's value after the change. */
    private final int after;

    /**
     * Creates a new StatisticChange.
     *
     * @param before the statistic's value before the change
     * @param after the statistic's value after the change
     */
    public StatisticChange(int before, int after) {
        this.before = before;
        this.after = after;
    }

    /**
     * Returns the statistic's value before the change.
     *
     * @return the value before the change
     */
    public int getBefore() {
        return before;
    }

    /**
     * Returns the statistic's value after the change.
     *
     * @return the value after the change
     */
    public int getAfter() {
        return after;
    }

    /**
     * Returns how much the statistic actually changed by, which can be less than
     * the requested amount when the value was capped at its maximum.
     *
     * @return the actual change
     */
    public int getAmount() {
        return after - before;
    }

    /**
     * Describes the change without a unit, e.g. "2 (10 -> 12)".
     *
     * @return the description
     */
    public String describe() {
        return getAmount() + " " + range();
    }

    /**
     * Describes the change with a unit, e.g. "5 HP (80 -> 85)".
     *
     * @param unit the unit shown after the amount, e.g. "HP" or "stamina"
     * @return the description
     */
    public String describe(String unit) {
        return getAmount() + " " + unit + " " + range();
    }

    /**
     * Formats the before and after values as "(before -> after)".
     *
     * @return the formatted range
     */
    private String range() {
        return "(" + before + " -> " + after + ")";
    }
}
