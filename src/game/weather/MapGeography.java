package game.weather;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Places a game map on the real world, so that each tile has a latitude and a
 * longitude.
 * <p>
 * The top-left tile sits at a chosen origin. Each step right adds a fixed
 * number of degrees of longitude, and each step down takes away the same
 * number of degrees of latitude, as on a real map where north is up. So the
 * place asked about changes whenever an actor moves.
 */
public class MapGeography {

    /** The highest latitude on Earth; latitudes beyond it are cut back to it. */
    private static final double MAX_LATITUDE = 90.0;

    /** The full turn of longitude, used to wrap longitudes past 180. */
    private static final double FULL_TURN = 360.0;

    /** The half turn of longitude, the edge of the -180 to 180 range. */
    private static final double HALF_TURN = 180.0;

    /** The latitude of the top-left tile. */
    private final double originLatitude;

    /** The longitude of the top-left tile. */
    private final double originLongitude;

    /** How many degrees one tile covers, both across and down. */
    private final double degreesPerTile;

    /**
     * Places a map on the real world.
     *
     * @param originLatitude the latitude of the top-left tile, from -90 to 90
     * @param originLongitude the longitude of the top-left tile, from -180 to 180
     * @param degreesPerTile how many degrees one tile covers; must be positive
     * @throws IllegalArgumentException if the origin is not on Earth or the
     *                                  step is not positive
     */
    public MapGeography(double originLatitude, double originLongitude, double degreesPerTile) {
        if (Math.abs(originLatitude) > MAX_LATITUDE || Math.abs(originLongitude) > HALF_TURN) {
            throw new IllegalArgumentException("The origin must be a real place on Earth");
        }
        if (!(degreesPerTile > 0)) {
            throw new IllegalArgumentException("Each tile must cover a positive number of degrees");
        }
        this.originLatitude = originLatitude;
        this.originLongitude = originLongitude;
        this.degreesPerTile = degreesPerTile;
    }

    /**
     * Finds the latitude of a tile. Going down the map goes south; a result
     * past a pole is cut back to the pole.
     *
     * @param tile the tile
     * @return its latitude, from -90 to 90
     */
    public double latitudeOf(Location tile) {
        double latitude = originLatitude - tile.y() * degreesPerTile;
        return Math.max(-MAX_LATITUDE, Math.min(MAX_LATITUDE, latitude));
    }

    /**
     * Finds the longitude of a tile. Going right on the map goes east; a
     * result past 180 wraps round to -180, as it does on Earth.
     *
     * @param tile the tile
     * @return its longitude, from -180 up to but not including 180
     */
    public double longitudeOf(Location tile) {
        double longitude = originLongitude + tile.x() * degreesPerTile;
        double wrapped = (longitude + HALF_TURN) % FULL_TURN;
        if (wrapped < 0) {
            wrapped += FULL_TURN;
        }
        return wrapped - HALF_TURN;
    }
}
