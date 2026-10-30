package game.positions.mine;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.items.mine.CopperOre;
import game.positions.Chargeable;
import game.positions.area.AreaFactory;

/**
 * A copper node. It cannot be entered, so it has to be broken to clear the
 * way. Breaking it drops copper ore and charges every adjacent ground that is
 * capable of holding a charge.
 */
public class CopperNode extends BreakableGround {

    /** Builds the Area of tiles around this node that may get charged. */
    private final AreaFactory areaFactory;

    /**
     * Creates a new CopperNode.
     *
     * @param areaFactory builds the Area of tiles around this node that may get charged
     */
    public CopperNode(AreaFactory areaFactory) {
        super('N', "Copper node");
        this.areaFactory = areaFactory;
    }

    /**
     * @return a new CopperOre
     */
    @Override
    protected Item createDrop() {
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
    protected String afterBreaking(Actor actor, Location location) {
        int chargedCount = 0;
        for (Location nearby : areaFactory.adjacentTo(location).tiles()) {
            Chargeable chargeable = nearby.getGroundAs(Chargeable.class);
            if (chargeable != null) {
                nearby.setGround(chargeable.getChargedResult());
                chargedCount++;
            }
        }
        return chargedCount + " nearby ground(s) become charged.";
    }
}
