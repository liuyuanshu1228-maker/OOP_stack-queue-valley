package game.travel;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.random.RandomUtils;

/**
 * A ladder linking one map to another. Climbing it risks a fall, but a fall
 * never stops the journey: the Actor still arrives, just hurt.
 */
public class Ladder extends TravelGround {

    /** Chance (out of 100) that the Actor loses their footing. */
    private static final int FALL_CHANCE_PERCENT = 50;
    /** Damage taken by an Actor who falls. */
    private static final int FALL_DAMAGE = 25;

    /**
     * Creates a new Ladder.
     */
    public Ladder() {
        super('=', "Ladder");
    }

    /**
     * Rolls the chance that the Actor falls on arrival and, if they do, hurts them.
     *
     * @param actor the Actor who has just arrived
     * @param landing the Location the Actor landed on
     * @return a description of the fall, or an empty string if the Actor kept their footing
     */
    @Override
    public String onArrival(Actor actor, Location landing) {
        if (!RandomUtils.rollPercent(FALL_CHANCE_PERCENT)) {
            return "";
        }
        actor.hurt(FALL_DAMAGE);
        return actor + " loses their footing and falls, taking " + FALL_DAMAGE + " damage.";
    }
}
