package game.creaturestate;

import edu.monash.fit2099.engine.positions.Location;

import java.util.Optional;

/**
 * Capability interface for an Actor that remembers the perch it last settled
 * on, so a state can send it back there.
 * <p>
 * State objects are shared between creatures, so this per-creature memory
 * lives on the Actor itself rather than on the state.
 */
public interface Roosting {

    /**
     * Returns the perch this Actor last settled on.
     *
     * @return the perch, or empty if this Actor has not settled anywhere yet
     */
    Optional<Location> getPerch();

    /**
     * Remembers a perch, replacing any earlier one.
     *
     * @param perch the Location this Actor has just settled on
     */
    void setPerch(Location perch);
}
