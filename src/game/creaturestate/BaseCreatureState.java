package game.creaturestate;

/**
 * Base class for creature states, giving them value equality by type.
 * <p>
 * A creature's state machine needs to know whether its next state is the same
 * state it is already in. Comparing state objects by reference would make that
 * answer depend on which instance a state happened to return, so two
 * {@code Fleeing} objects would wrongly count as a transition. This class instead
 * treats any two states of the same class as the same state, so a state can be
 * created as many times as needed and can still be subclassed freely.
 */
public abstract class BaseCreatureState implements CreatureState {

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
