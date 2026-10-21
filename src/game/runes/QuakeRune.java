package game.runes;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.Tillable;
import game.status.StunnedStatus;

import java.util.List;

/**
 * A Rune whose effect is an earthquake: every affected tile that can be
 * tilled is shaken into its tilled form (Dirt becomes Soil, exactly as if a
 * Farmer had hoed it), and every Actor caught on an affected tile is Stunned.
 * <p>
 * Reuses the existing {@code Tillable} capability rather than hardcoding
 * "Dirt becomes Soil" here: this rune does not need to know which Ground
 * types are tillable or what they till into, and automatically keeps working
 * if another Tillable ground is ever added (DRY, open/closed).
 */
public class QuakeRune implements Rune {

    /** How many turns an Actor caught in the quake is Stunned for. */
    private static final int STUN_DURATION_TURNS = 2;

    @Override
    public String applyTo(List<Location> affectedTiles, Actor activator) {
        int tilesShaken = 0;
        int actorsStunned = 0;
        for (Location tile : affectedTiles) {
            Tillable tillable = tile.getGroundAs(Tillable.class);
            if (tillable != null) {
                tile.setGround(tillable.getTillResult());
                tilesShaken++;
            }
            if (tile.containsAnActor()) {
                StunnedStatus.applyTo(tile.getActor(), STUN_DURATION_TURNS);
                actorsStunned++;
            }
        }
        return activator + " channels a Quake Rune, shaking " + tilesShaken
                + " tile(s) loose and stunning " + actorsStunned + " actor(s).";
    }
}
