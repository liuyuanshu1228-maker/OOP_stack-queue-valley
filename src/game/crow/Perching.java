package game.crow;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.WanderBehaviour;
import game.creaturestate.BaseCreatureState;
import game.creaturestate.CreatureState;
import game.positions.FreelyWaterable;
import game.positions.area.Area;

import java.util.ArrayList;
import java.util.List;

/**
 * The crow's default, idle state: wanders aimlessly around the map, watching
 * for food or for actors to mob. Its ordinary turn is a {@link Behaviour}, by
 * default the same {@link WanderBehaviour} that the animals and the Merchant use.
 * <p>
 * From here the crow moves to {@link Mobbing} if exactly one actor is nearby,
 * or to {@link Foraging} once food comes within sensing range; otherwise it
 * stays perching. {@link Mobbing} and {@link Fleeing} both settle back into
 * this state once their own conditions for leaving are met.
 */
public class Perching extends BaseCreatureState {

    /** Decides where the crow hops on an ordinary turn. */
    private final Behaviour<Actor, Action> wander;

    /**
     * Creates this state with the default wandering behaviour. A new instance
     * is created every time a transition returns this state; {@link BaseCreatureState}
     * gives every instance of the same class the same identity, so creating a
     * fresh one each time costs nothing.
     */
    public Perching() {
        this(new WanderBehaviour());
    }

    /**
     * Creates this state with the given behaviour for its ordinary turn.
     *
     * @param wander decides where the crow hops on an ordinary turn
     */
    public Perching(Behaviour<Actor, Action> wander) {
        this.wander = wander;
    }

    /**
     * Hops to a new perch, as chosen by the wandering behaviour, or stays put
     * if there is nowhere to go.
     *
     * @param creature the crow acting
     * @param map the map the crow is on
     * @return a description of what happened
     */
    @Override
    public String act(Actor creature, GameMap map) {
        Action hop = wander.operate(creature, map.locationOf(creature));

        if (hop == null) {
            return creature + " perches, with nowhere to go.";
        }

        hop.execute(creature, map);
        return creature + " hops to a new perch at " + map.locationOf(creature) + ".";
    }

    /**
     * Moves to Mobbing if exactly one other actor is visible within sensing
     * range and it is carrying something (a lone target worth harassing).
     * Moves to Foraging once food is
     * visible within sensing range. Otherwise stays perching.
     * <p>
     * Mobbing is checked first: if a lone actor is nearby the crow prioritises
     * the confrontation over scavenging for food.
     *
     * @param creature the crow whose next state is being decided
     * @param map the map the crow is on
     * @return the next state
     */
    @Override
    public CreatureState nextState(Actor creature, GameMap map) {
        CrowSensing sensing = new CrowSensing(creature, map.locationOf(creature));

        if (sensing.hasLoneTargetWithItems()) {
            return new Mobbing();
        }

        if (sensing.nearestFood().isPresent()) {
            return new Foraging();
        }

        return this;
    }

    /**
     * The effect of settling back into Perching after being in another
     * state: the crow's droppings count as a free watering for every crop on
     * its own tile and on each tile adjacent to it.
     *
     * @param creature the crow entering this state
     * @param map the map the crow is on
     * @return a description of the effect, listing every crop watered
     */
    @Override
    public String onEnter(Actor creature, GameMap map) {
        Location here = map.locationOf(creature);
        List<FreelyWaterable> watered = new ArrayList<>();
        for (Location location : Area.centredOn(here).tiles()) {
            FreelyWaterable waterable = location.getGroundAs(FreelyWaterable.class);
            if (waterable != null) {
                waterable.receiveFreeWatering();
                watered.add(waterable);
            }
        }

        if (watered.isEmpty()) {
            return creature + " settles back onto a perch at " + here
                    + ", with no crops within 1 tile to water.";
        }
        return creature + " settles back onto a perch at " + here
                + ", and its droppings count as a free watering for every crop within 1 tile: "
                + watered + ".";
    }

    /** @return the name of this state */
    @Override
    public String toString() {
        return "Perching";
    }
}