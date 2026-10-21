package game.runes;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Ground left behind when a rune stone is placed. It remembers the Rune it
 * carries (so a RunePattern can read it via {@link CarriesRune}) and the
 * ground it replaced, so that once it has triggered a formation it can give
 * that ground back -- the stone has done its job, and the tile is free
 * again.
 * <p>
 * A rune stone that is placed but never completes a formation simply stays
 * on the map as a RuneStone: it is still a valid "wing" for a formation
 * someone completes around it later.
 */
public class RuneStone extends Ground implements CarriesRune {

    private final Rune rune;
    private final Ground originalGround;

    /**
     * @param displayChar the character to display for this rune stone
     * @param name the name of this rune stone
     * @param rune the Rune this stone carries
     * @param originalGround the ground this stone replaces, kept so it can be restored later
     */
    public RuneStone(char displayChar, String name, Rune rune, Ground originalGround) {
        super(displayChar, name);
        this.rune = rune;
        this.originalGround = originalGround;
    }

    @Override
    public Rune getRune() {
        return rune;
    }

    /**
     * Restores the given Location back to the ground this rune stone
     * replaced. Called once this stone has triggered a successful formation.
     * <p>
     * Deliberately not named {@code revertAt} or {@code tick}-driven: REQ3's
     * planned {@code TemporaryGround} base class restores its ground after a
     * fixed number of turns regardless of what the player does, whereas a
     * RuneStone never expires on its own -- it only restores its ground the
     * moment it triggers a formation. The two are different contracts with
     * different callers, so RuneStone does not extend TemporaryGround, and
     * this method keeps a distinct name so the two are never confused.
     *
     * @param location the location this rune stone occupies
     */
    public void restoreOriginalGround(Location location) {
        location.setGround(originalGround);
    }
}
