package game.pestcontrol;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.random.RandomUtils;

/**
 * A jar of ladybugs. Using it releases a few ladybugs on the free tiles
 * around the user, and the jar is used up.
 */
public class LadybugJar extends DeterrentItem {

    /** How many ladybugs the jar holds. */
    private final int ladybugs = 3;

    /** The randomness handed to every Ladybug this jar releases. */
    private final RandomUtils randomUtils;

    /**
     * Creates a jar of ladybugs, shown as 'j'.
     *
     * @param randomUtils the randomness handed to every Ladybug this jar releases
     */
    public LadybugJar(RandomUtils randomUtils) {
        super("Jar of Ladybugs", 'j');
        this.randomUtils = randomUtils;
    }

    /**
     * Releases ladybugs on the free tiles around here, up to the number the
     * jar holds.
     *
     * @param here the tile the user stands on
     * @param map the map containing that tile
     * @return what happened
     */
    @Override
    public String deterAround(Location here, GameMap map) {
        int released = 0;
        for (Exit exit : here.getExits()) {
            if (released == ladybugs) {
                break;
            }
            Location tile = exit.getDestination();
            Ladybug ladybug = new Ladybug(randomUtils);
            if (tile.canActorEnter(ladybug)) {
                try {
                    tile.addActor(ladybug);
                    released++;
                } catch (GameEngineException e) {
                    // The tile was taken; try the next one.
                }
            }
        }
        return "The jar opens and " + released + " ladybugs fly out around " + here + ".";
    }
}
