package game.runes;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * A Rune whose effect is growth: every affected tile that is bare Soil is
 * meant to sprout a crop at its earliest growth stage.
 * <p>
 * <b>Blocked on REQ1.</b> The team's feature proposal commits to growing a
 * Seedling/Sprout (REQ1's early growth stages), not a mature crop --
 * substituting a mature crop would not match the proposal and would cost the
 * Rule 4 mark for this requirement. REQ1's Plant/Seedling/Sprout classes do
 * not exist in the codebase yet, and this class must not invent them (REQ1
 * is a teammate's file, not this requirement's). Until that skeleton lands,
 * this Rune deliberately does nothing rather than doing the wrong thing.
 * Once Seedling/Sprout exist, replace {@link #applyTo} with "plant the
 * earliest growth stage on every affected tile that is bare Soil".
 */
public class VerdantRune implements Rune {

    @Override
    public String applyTo(List<Location> affectedTiles, Actor activator) {
        return activator + " channels a Verdant Rune, but it has no effect yet "
                + "(awaiting REQ1's Plant/Seedling classes).";
    }
}
