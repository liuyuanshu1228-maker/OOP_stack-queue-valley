package game.pestcontrol;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.pests.PestPresent;
import game.positions.TemporaryGround;
import game.positions.area.Area;

/**
 * A set pest trap. Each turn it captures the pests on it or next to it, then
 * pulls every pest within two tiles one tile closer. It turns back into the
 * ground it covered after two captures, or when it expires.
 * <p>
 * A ground's tick returns nothing, so the trap prints its own report.
 */
public class TrapGround extends TemporaryGround {

    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = 'X';

    /** The name of this ground. */
    private static final String NAME = "Pest Trap";

    /** How many turns the trap lasts. */
    private static final int DURATION_TURNS = 10;

    /** How many pests the trap can capture before it is used up. */
    private static final int MAX_CAPTURES = 2;

    /** How far away the trap pulls pests from. */
    private static final int PULL_RADIUS = 2;

    /** Finds the pests around the trap. */
    private final PestPresent pestFinder = new PestPresent();

    /** How many more pests the trap can capture. */
    private int capturesLeft = MAX_CAPTURES;

    /**
     * Sets a trap on a ground.
     *
     * @param originalGround the ground to restore when the trap is used up
     */
    public TrapGround(Ground originalGround) {
        super(DISPLAY_CHAR, NAME, originalGround, DURATION_TURNS);
    }

    /**
     * Captures nearby pests, then, if the trap is not used up, pulls the
     * farther ones closer. Prints what happened, and turns back once all
     * captures are used.
     *
     * @param location the tile this trap is on
     */
    @Override
    protected void tickEffect(Location location) {
        String report = capturePests(location);
        if (capturesLeft > 0) {
            report += pullPests(location);
        }
        if (!report.isEmpty()) {
            new Display().println(NAME + " at " + location + ":" + report);
        }
        if (capturesLeft == 0) {
            revertAt(location);
        }
    }

    /**
     * Removes the pests on or next to the trap, up to the captures left.
     *
     * @param location the tile this trap is on
     * @return what was captured, or an empty string
     */
    private String capturePests(Location location) {
        StringBuilder report = new StringBuilder();
        for (Location tile : Area.centredOn(location).filter(pestFinder).tiles()) {
            if (capturesLeft == 0) {
                break;
            }
            Actor pest = tile.getActor();
            location.map().removeActor(pest);
            capturesLeft--;
            report.append(" captures ").append(pest).append(" at ").append(tile).append(".");
        }
        return report.toString();
    }

    /**
     * Moves every pest two tiles away one tile closer, if it can enter that
     * tile.
     *
     * @param location the tile this trap is on
     * @return what was pulled, or an empty string
     */
    private String pullPests(Location location) {
        GameMap map = location.map();
        StringBuilder report = new StringBuilder();
        for (Location tile : Area.within(location, PULL_RADIUS).filter(pestFinder).tiles()) {
            if (isNextTo(tile, location)) {
                continue;
            }
            int dx = Integer.signum(location.x() - tile.x());
            int dy = Integer.signum(location.y() - tile.y());
            Location closer = map.at(tile.x() + dx, tile.y() + dy);
            Actor pest = tile.getActor();
            if (closer.canActorEnter(pest)) {
                map.moveActor(pest, closer);
                report.append(" pulls ").append(pest).append(" to ").append(closer).append(".");
            }
        }
        return report.toString();
    }

    /**
     * @param tile a tile near the trap
     * @param location the tile this trap is on
     * @return true if the tile is next to the trap
     */
    private boolean isNextTo(Location tile, Location location) {
        return Math.abs(tile.x() - location.x()) <= 1 && Math.abs(tile.y() - location.y()) <= 1;
    }
}
