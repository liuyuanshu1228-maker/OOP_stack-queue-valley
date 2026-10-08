package game.crow;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.area.Area;
import game.positions.area.HoldsActor;

import java.util.Optional;

/**
 * What a crow can sense from where it stands: food, other actors, and whether
 * it is crowded. It answers the questions the crow's states ask when deciding
 * what to do, so each state's transition logic stays a short, readable
 * expression instead of repeating the same checks.
 * <p>
 * One instance describes one crow at one Location; the states are shared
 * between crows, so they create a new one each time they need to sense.
 */
class CrowSensing {

    /** The number of actors within sensing range that makes the crow feel crowded. */
    static final int CROWD_SIZE = 2;

    /** The creature that is sensing. */
    private final Actor creature;

    /** Where the creature is sensing from. */
    private final Location here;

    /**
     * Creates the senses of a creature at a Location.
     *
     * @param creature the creature doing the sensing
     * @param here the Location the creature senses from
     */
    CrowSensing(Actor creature, Location here) {
        this.creature = creature;
        this.here = here;
    }

    /**
     * Finds an Item at the creature's own Location that is worth foraging.
     *
     * @return the first forageable Item here, or empty if there is none
     * @see ForageableItems
     */
    Optional<Item> forageableItemHere() {
        return new ForageableItems(creature).firstAt(here);
    }

    /**
     * Whether there is an Item at the creature's own Location worth foraging.
     *
     * @return true if there is at least one forageable Item here
     */
    boolean isOnFood() {
        return forageableItemHere().isPresent();
    }

    /**
     * Whether there is any forageable Item within sensing range, including
     * the creature's own Location.
     *
     * @return true if forageable food is within sensing range
     */
    boolean foodWithinRange() {
        return isOnFood() || nearestFood().isPresent();
    }

    /**
     * Finds the closest Location within sensing range (excluding the creature's
     * own) that holds a forageable Item.
     *
     * @return the closest Location with forageable food, or empty if there is none
     */
    Optional<Location> nearestFood() {
        return withinRange().filter(new ForageableItems(creature)).nearestTo(here);
    }

    /**
     * Counts the other actors within sensing range.
     *
     * @return the number of actors within sensing range, not counting one standing here
     */
    int actorsWithinRange() {
        return withinRange().filter(new HoldsActor()).tiles().size();
    }

    /**
     * Whether no actor is within sensing range.
     *
     * @return true if the creature has nobody nearby
     */
    boolean isSafe() {
        return actorsWithinRange() == 0;
    }

    /**
     * Whether enough actors are within sensing range for the creature to feel crowded.
     *
     * @return true if at least {@value #CROWD_SIZE} actors are within sensing range
     */
    boolean isCrowded() {
        return actorsWithinRange() >= CROWD_SIZE;
    }

    /**
     * Finds the closest Location within sensing range (excluding the creature's
     * own) that holds an actor.
     *
     * @return the closest Location holding an actor, or empty if there is none
     */
    Optional<Location> nearestActor() {
        return withinRange().filter(new HoldsActor()).nearestTo(here);
    }

    /**
     * Whether exactly one actor is within sensing range and that actor is
     * carrying at least one item: a lone target worth mobbing.
     *
     * @return true if there is a lone actor nearby with a non-empty inventory
     */
    boolean hasLoneTargetWithItems() {
        Optional<Location> target = nearestActor();
        return actorsWithinRange() == 1
                && target.isPresent()
                && !target.get().getActor().getInventory().getItems().isEmpty();
    }

    /**
     * @return the tiles within the crow's sensing range, not including its own
     */
    private Area withinRange() {
        return Area.within(here, Crow.SENSING_RADIUS);
    }
}
