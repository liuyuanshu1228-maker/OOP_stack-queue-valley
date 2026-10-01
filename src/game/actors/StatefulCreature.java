package game.actors;

/**
 * Capability interface for an Actor whose behaviour is driven by a
 * {@link CreatureState} state machine.
 */
public interface StatefulCreature {

    /**
     * Returns the state this creature is currently in.
     *
     * @return the current state
     */
    CreatureState getState();

    /**
     * Switches this creature to a new state.
     *
     * @param state the state to switch to
     */
    void setState(CreatureState state);
}
