package game.creaturestate;

import edu.monash.fit2099.engine.actors.Actor;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

import java.util.Optional;

/**
 * Base class for creature states, giving them value equality by type.
 * <p>
 * A creature's state machine needs to know whether its next state is the same
 * state it is already in. Comparing state objects by reference would make that
 * answer depend on which instance a state happened to return, so two
 * {@code Fleeing} objects would wrongly count as a transition. This class instead
 * treats any two states of the same class as the same state, so a state can be
 * created as many times as needed and can still be subclassed freely.
 * <p>
 * A state holds no data of its own, so it asks the acting creature for the
 * {@link AreaFactory} and {@link RandomUtils} it needs, through the
 * {@link AreaFactorySource} and {@link RandomnessSource} capabilities. Those
 * two lookups are written once here, for every state to share.
 */
public abstract class BaseCreatureState implements CreatureState {

    /**
     * Creates a creature state. Only subclasses create states.
     */
    protected BaseCreatureState() {
    }

    /**
     * Asks the acting creature for the AreaFactory it uses to build Areas.
     *
     * @param creature the creature whose turn it is
     * @return the creature's AreaFactory
     * @throws IllegalStateException if the creature has no AreaFactorySource capability
     */
    protected AreaFactory areaFactoryOf(Actor creature) {
        Optional<AreaFactorySource> source = creature.asCapability(AreaFactorySource.class);
        if (source.isEmpty()) {
            throw new IllegalStateException(creature + " has no AreaFactorySource");
        }
        return source.get().areaFactory();
    }

    /**
     * Asks the acting creature for the randomness it uses.
     *
     * @param creature the creature whose turn it is
     * @return the creature's RandomUtils
     * @throws IllegalStateException if the creature has no RandomnessSource capability
     */
    protected RandomUtils randomUtilsOf(Actor creature) {
        Optional<RandomnessSource> source = creature.asCapability(RandomnessSource.class);
        if (source.isEmpty()) {
            throw new IllegalStateException(creature + " has no RandomnessSource");
        }
        return source.get().randomUtils();
    }

    /**
     * Two creature states are the same state when they are of the same class.
     *
     * @param other the object to compare with
     * @return true if other is a creature state of exactly the same class
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        return getClass() == other.getClass();
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}: every state of
     * the same class has the same hash code.
     *
     * @return the hash code of this state's class
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
