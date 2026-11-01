package game.pestcontrol;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.Unplanted;
import game.pests.PestPresent;
import game.positions.Plantable;
import game.positions.Tillable;
import game.positions.area.AreaFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A bag of salt. Using it turns the bare Soil and Dirt around the user into
 * salt ground. Pests on those tiles are hurt and pushed out.
 */
public class SaltBarrier extends DeterrentItem {

    /** The name of this item. */
    private static final String NAME = "Bag of Salt";

    /** The character shown on the map. */
    private static final char DISPLAY_CHAR = 'f';

    /** Damage dealt to a pest caught on a tile when it is salted. */
    private static final int SALT_DAMAGE = 2;

    /** Finds the pests on the salted tiles. */
    private final PestPresent pestFinder = new PestPresent();

    /** Builds the Area of tiles around the user that get salted. */
    private final AreaFactory areaFactory;

    /**
     * Creates a bag of salt.
     *
     * @param areaFactory builds the Area of tiles around the user that get salted
     */
    public SaltBarrier(AreaFactory areaFactory) {
        super(NAME, DISPLAY_CHAR);
        this.areaFactory = areaFactory;
    }

    /**
     * Salts the bare farm ground around here, then hurts and pushes out any
     * pest standing on it.
     *
     * @param here the tile the user stands on
     * @param map the map containing that tile
     * @return what happened
     */
    @Override
    public String deterAround(Location here, GameMap map) {
        List<Location> salted = new ArrayList<>();
        for (Location tile : areaFactory.adjacentTo(here).filter(new Unplanted()).tiles()) {
            if (tile.getGroundAs(Plantable.class) != null || tile.getGroundAs(Tillable.class) != null) {
                tile.setGround(new SaltGround(tile.getGround()));
                salted.add(tile);
            }
        }
        StringBuilder report = new StringBuilder("Salt covers " + salted.size() + " tiles around " + here + ".");
        for (Location tile : salted) {
            if (pestFinder.isMetBy(tile)) {
                report.append(" ").append(hurtAndPushOut(tile.getActor(), tile, map));
            }
        }
        return report.toString();
    }

    /**
     * Hurts a pest on a salted tile and moves it to a neighbouring tile it can
     * enter, if there is one.
     *
     * @param pest the pest on the salted tile
     * @param tile the salted tile
     * @param map the map containing the tile
     * @return what happened to the pest
     */
    private String hurtAndPushOut(Actor pest, Location tile, GameMap map) {
        pest.hurt(SALT_DAMAGE);
        for (Exit exit : tile.getExits()) {
            Location destination = exit.getDestination();
            if (destination.canActorEnter(pest)) {
                map.moveActor(pest, destination);
                return pest + " is burnt by the salt and pushed out to " + destination + ".";
            }
        }
        return pest + " is burnt by the salt at " + tile + " but has nowhere to go.";
    }
}
