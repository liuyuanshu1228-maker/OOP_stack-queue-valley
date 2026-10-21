package game.runes;

/**
 * Capability interface for Ground that carries a Rune, i.e. a rune stone.
 * <p>
 * This exists so that the RunePattern implementations can ask "is there a
 * rune stone on this tile" via {@code Location.getGroundAs(CarriesRune.class)}
 * without knowing about {@link RuneStone} itself, and without instanceof.
 * {@link RuneStone} is currently the only Ground that implements it, but the
 * patterns depend on this contract, not on that one class.
 */
public interface CarriesRune {

    /**
     * @return the Rune this ground carries
     */
    Rune getRune();
}
