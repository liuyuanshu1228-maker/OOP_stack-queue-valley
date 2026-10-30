package game.crow;

import game.positions.area.AreaFactory;

/**
 * Capability interface for a creature that can provide an {@link AreaFactory}.
 * Lets the crow's stateless states obtain an AreaFactory from the acting
 * creature at the point of use, instead of storing one on the state itself:
 * a {@link game.creaturestate.CreatureState} is shared between crows and must
 * stay constructible with no arguments, so it cannot hold its own AreaFactory
 * field.
 */
public interface AreaFactorySource {

    /**
     * @return the AreaFactory this creature uses to build Areas
     */
    AreaFactory areaFactory();
}
