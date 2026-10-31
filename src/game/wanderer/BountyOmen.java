package game.wanderer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.plants.Plant;
import game.positions.area.AreaFactory;
import game.status.GrowthPausedStatus;
import game.weather.WeatherReading;

/**
 * The omen of a rainy forecast: gives every {@link Plant} around the Wanderer
 * one bonus attempt at whatever it would normally do on its turn (growing,
 * self-replicating, or producing), as if an extra turn had passed for it.
 * <p>
 * Uses {@link Plant#attemptGrowth(Location)} exactly as its own documentation
 * invites: an external effect forcing an extra growth attempt without ageing
 * the plant or printing its result twice. A plant currently paused by a
 * {@link GrowthPausedStatus} (e.g. from a {@link FrostOmen}) is skipped, so
 * this omen never grants a bonus turn to a plant that frost says should not
 * be growing right now.
 */
public class BountyOmen implements ForecastOmen {

    /** How many tiles from the Wanderer this omen reaches. */
    private static final int RADIUS_TILES = 2;

    /** Tomorrow's forecast reading that triggered this omen. */
    private final WeatherReading forecast;
    /** Builds the Area of tiles this omen affects. */
    private final AreaFactory areaFactory;

    /**
     * Creates a bounty omen.
     *
     * @param forecast tomorrow's forecast reading that triggered this omen
     * @param areaFactory builds the Area of tiles this omen affects
     */
    public BountyOmen(WeatherReading forecast, AreaFactory areaFactory) {
        this.forecast = forecast;
        this.areaFactory = areaFactory;
    }

    /**
     * Gives every unpaused Plant within range of {@code here} one bonus
     * growth attempt.
     *
     * @param asker the actor who asked for the forecast
     * @param here the tile the Wanderer stands on
     * @return a description of the bounty promised, including {@code here}
     */
    @Override
    public String bringUpon(Actor asker, Location here) {
        int plantsAdvanced = 0;
        for (Location tile : areaFactory.within(here, RADIUS_TILES).tiles()) {
            Plant plant = tile.getGroundAs(Plant.class);
            if (plant == null || plant.hasStatus(GrowthPausedStatus.class)) {
                continue;
            }
            if (!plant.attemptGrowth(tile).isEmpty()) {
                plantsAdvanced++;
            }
        }
        return "The Wanderer promises " + asker + " a bountiful " + forecast.getCondition()
                + "y day tomorrow: " + plantsAdvanced + " plants around " + here
                + " get a bonus turn of growth.";
    }
}
