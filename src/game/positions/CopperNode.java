package game.positions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.items.CopperOre;

/**
 * A copper node. It cannot be entered, so it has to be broken to clear the
 * way. Breaking it drops copper ore and charges every adjacent ground that is
 * capable of holding a charge.
 */
public class CopperNode extends BreakableGround {

    /**
     * Constructor.
     */
    public CopperNode() {
        super('N', "Copper node");
    }

    /**
     * @return a new CopperOre
     */
    @Override
    Item createDrop() {
        return new CopperOre();
    }

    /**
     * Charges every adjacent ground that is capable of holding a charge.
     *
     * @param actor the Actor who broke the copper node
     * @param location the Location the copper node occupied
     * @return a description of what happened
     */
    @Override
    String afterBreaking(Actor actor, Location location) {
        int chargedCount = 0;
        for (Location nearby : location.getNearbyLocations(1)) {
            Chargeable chargeable = nearby.getGroundAs(Chargeable.class);
            if (chargeable != null) {
                nearby.setGround(chargeable.getChargedResult());
                chargedCount++;
            }
        }
        return chargedCount + " nearby ground(s) become charged.";
    }
}
