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

    /** The name of this item. */
    private static final String NAME = "Jar of Ladybugs";

    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = 'j';

    /** How many ladybugs the jar holds. */
    private static final int LADYBUGS = 3;

    /** The randomness handed to every Ladybug this jar releases. */
    private final RandomUtils randomUtils;

    /**
     * Creates a jar of ladybugs.
     *
     * @param randomUtils the randomness handed to every Ladybug this jar releases
     */
    public LadybugJar(RandomUtils randomUtils) {
        super(NAME, DISPLAY_CHAR);
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
            if (released == LADYBUGS) {
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
