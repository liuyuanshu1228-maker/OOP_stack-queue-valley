package game.plants;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.farm.Dirt;
import game.status.GrowthPausedStatus;

import java.util.List;

/**
 * Abstract base class for every plant, at any growth stage.
 * <p>
 * A plant is a type of {@link Ground} with its own age: it is 0 when the plant
 * is placed on the map and goes up by 1 every turn. Only the plant itself
 * changes its age, so there is no setter.
 * <p>
 * Each plant has a list of {@link PlantAbility abilities} in priority order.
 * Every turn it tries them from the highest priority down and stops at the
 * first one that actually does something, so a plant does at most one thing
 * per turn ({@link #attemptGrowth}). What it did is displayed to the player;
 * a turn where nothing happens is not reported. While the plant has a
 * {@link GrowthPausedStatus}, it still ages but does not try any of its
 * abilities.
 * <p>
 * Every plant is {@link Regressible}. By default a regressed plant dies and
 * its tile becomes {@link Dirt}, as when a crop is removed; plants with an
 * earlier growth stage override {@link #previousStage()} to return it
 * instead.
 */
public abstract class Plant extends Ground implements Regressible {

    /**
     * Ground has no Display passed to its tick, so each plant keeps its own
     * to report what it does.
     */
    private final Display display = new Display();

    /**
     * The number of turns this plant has been on the map.
     */
    private int age = 0;

    /**
     * The abilities of this plant, from the highest priority to the lowest.
     */
    private final List<PlantAbility> abilities;

    /**
     * Creates a new Plant with an age of 0.
     *
     * @param displayChar character to display for this plant
     * @param name the name of this plant
     * @param abilities the abilities of this plant, from the highest priority to the lowest
     */
    protected Plant(char displayChar, String name, PlantAbility... abilities) {
        super(displayChar, name);
        this.abilities = List.of(abilities);
    }

    /**
     * How many turns this plant has been on the map.
     *
     * @return the age of this plant, in turns
     */
    public int getAge() {
        return age;
    }

    /**
     * Ages this plant by one turn, then tries this plant's abilities via
     * {@link #attemptGrowth} and displays the result.
     *
     * @param location the location of this plant
     */
    @Override
    public void tick(Location location) {
        age++;
        String report = attemptGrowth(location);
        if (!report.isEmpty()) {
            display.println(report);
        }
    }

    /**
     * Tries this plant's abilities, from the highest priority down, and
     * stops at the first one that does something. While the plant's growth is
     * paused, no ability is tried, so every caller (the plant's own turn or a
     * magical effect) obeys the pause. Does not age the plant and
     * does not display the result, so a caller forcing an extra growth
     * attempt (e.g. a magical effect) gets the description back to use
     * however it needs to, instead of this plant ageing an extra turn or the
     * result being printed twice.
     *
     * @param location the location of this plant
     * @return a description of what happened, or "" if no ability did anything
     */
    public String attemptGrowth(Location location) {
        if (hasStatus(GrowthPausedStatus.class)) {
            return "";
        }
        for (PlantAbility ability : abilities) {
            String report = ability.tryPerform(this, location);
            if (!report.isEmpty()) {
                return report;
            }
        }
        return "";
    }

    /**
     * The ground this plant falls back to when it is regressed. Defaults to
     * Dirt, meaning the plant dies and its tile must be tilled again before
     * anything can grow there; override for plants that have an earlier
     * growth stage.
     *
     * @return a new instance of the earlier stage
     */
    protected Ground previousStage() {
        return new Dirt();
    }

    /**
     * Replaces this plant with its earlier growth stage.
     *
     * @param location the location of this plant
     * @return a description of what happened, to be displayed to the player
     */
    @Override
    public String regress(Location location) {
        Ground previous = previousStage();
        location.setGround(previous);
        return this + " at " + location + " is set back to " + previous + ".";
    }
}
