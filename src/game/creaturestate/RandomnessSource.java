package game.creaturestate;

import game.random.RandomUtils;

/**
 * Capability interface for a creature that can provide a {@link RandomUtils}.
 * Lets the crow's stateless states obtain randomness from the acting
 * creature at the point of use, instead of storing it on the state itself:
 * a {@link CreatureState} is shared between crows and must
 * stay constructible with no arguments, so it cannot hold its own RandomUtils
 * field.
 */
public interface RandomnessSource {

    /**
     * Returns the randomness of this creature.
     *
     * @return the randomness this creature uses
     */
    RandomUtils randomUtils();
}
