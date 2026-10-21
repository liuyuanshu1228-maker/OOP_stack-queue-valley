package game.runes;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Places a rune stone at the Actor's current Location, then checks whether
 * it completes any known rune formation.
 * <p>
 * If it does: the formation's Rune is applied to the tiles its matching
 * RunePattern reaches (twice over, if the stone was placed on an Altar's
 * one-time boost), the pattern's own bonus effect fires, and the triggering
 * stone reverts to the ground it replaced -- it has done its job. If it
 * does not complete anything, the stone simply stays on the map, ready to
 * take part in a later formation.
 * <p>
 * This is the one place that needs to know about every known RunePattern,
 * so that list lives here rather than inside any one pattern (a pattern
 * should not need to know what other patterns exist).
 */
public class PlaceRuneStoneAction extends Action {

    /** Every shape a rune formation can take, checked in this order. */
    private static final List<RunePattern> KNOWN_PATTERNS =
            List.of(new LinePattern(), new TrianglePattern(), new SquarePattern());

    private final RuneStoneItem item;
    private final Rune rune;

    /**
     * @param item the rune stone item being placed
     * @param rune the Rune carried by {@code item}
     */
    public PlaceRuneStoneAction(RuneStoneItem item, Rune rune) {
        this.item = item;
        this.rune = rune;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Location location = map.locationOf(actor);
        Boostable boostable = location.getGroundAs(Boostable.class);
        boolean boosted = boostable != null && boostable.consumeBoost();

        Ground originalGround = location.getGround();
        RuneStone stone = new RuneStone(item.getDisplayChar(), item.toString(), rune, originalGround);
        location.setGround(stone);
        actor.getInventory().remove(item);

        StringBuilder result = new StringBuilder();
        result.append(actor).append(" sets down a ").append(item).append(" at ").append(location).append(".");

        for (RunePattern pattern : KNOWN_PATTERNS) {
            if (pattern.matches(location)) {
                List<Location> affected = pattern.affectedTiles(location);
                result.append(' ').append(rune.applyTo(affected, actor));
                if (boosted) {
                    result.append(' ').append(rune.applyTo(affected, actor))
                            .append(" (boosted by the Altar!)");
                }
                result.append(' ').append(pattern.applyBonusEffect(location, map));
                stone.revertAt(location);
                break;
            }
        }
        return result.toString();
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " places a " + item + " here";
    }
}
