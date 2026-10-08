package game.creaturestate;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * One state in a stateful creature's state machine.
 * <p>
 * Each implementing class owns exactly one state's three responsibilities:
 * its ordinary per-turn behaviour, the condition(s) that decide which state
 * comes next, and the one-off effect that fires at the moment the creature
 * transitions into it. States are expected to be stateless/shared (the
 * classic State pattern): any data that varies per creature belongs on the
 * creature itself, not on the state instance. Implementations should extend
 * {@link BaseCreatureState}, which makes two states of the same class equal.
 */
public interface CreatureState {

    /**
     * The creature's ordinary behaviour for a turn spent in this state.
     * Only called when the creature is staying in this state (see
     * {@link #nextState}); never called on the turn the creature enters it.
     *
     * @param creature the creature acting
     * @param map the map the creature is on
     * @return a description of what happened, to be displayed to the player
     */
    String act(Actor creature, GameMap map);

    /**
     * Decides which state the creature should be in next, based on the
     * creature's current situation. Returning {@code this}, or any state equal
     * to it, means the creature stays in this state.
     *
     * @param creature the creature whose next state is being decided
     * @param map the map the creature is on
     * @return the state the creature should be in next
     */
    CreatureState nextState(Actor creature, GameMap map);

    /**
     * Applies this state's one-off effect, triggered at the moment the
     * creature transitions into it. Called instead of {@link #act} on the
     * turn the transition happens.
     *
     * @param creature the creature entering this state
     * @param map the map the creature is on
     * @return a description of the transition's effect, to be displayed to the player
     */
    String onEnter(Actor creature, GameMap map);
}