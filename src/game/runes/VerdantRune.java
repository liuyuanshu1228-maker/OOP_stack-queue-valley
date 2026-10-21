package game.runes;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.crops.Cauliflower;
import game.crops.Crop;
import game.crops.Melon;
import game.crops.Parsnip;
import game.crops.Strawberry;
import game.crops.Tomato;
import game.positions.farm.Soil;
import game.random.RandomUtils;

import java.util.List;
import java.util.function.Supplier;

/**
 * A Rune whose effect is growth: every affected tile that is bare Soil
 * sprouts a randomly chosen crop.
 * <p>
 * Soil has no capability interface of its own (nothing currently needs one),
 * so unlike {@link QuakeRune}'s use of {@code Tillable}, this cannot ask
 * "can this tile grow a crop" via {@code getGroundAs}. Checking the exact
 * class with {@code getClass().equals(Soil.class)} is used instead of
 * {@code instanceof} -- it is not a type-hierarchy check, so it does not
 * fall under the "no instanceof" rule, and the same technique already
 * appears elsewhere in this codebase (e.g. BaseCreatureState's equals/hashCode)
 * for exactly this "is this the same concrete type" question. If a
 * {@code Plantable} capability is ever added to Soil, this should switch to
 * {@code getGroundAs(Plantable.class)} for consistency.
 */
public class VerdantRune implements Rune {

    /** The crop types this rune may plant. */
    private static final List<Supplier<Crop>> PLANTABLE_CROPS = List.of(
            Strawberry::new, Cauliflower::new, Melon::new, Tomato::new, Parsnip::new);

    @Override
    public String applyTo(List<Location> affectedTiles, Actor activator) {
        int grown = 0;
        for (Location tile : affectedTiles) {
            Ground ground = tile.getGround();
            if (ground != null && ground.getClass().equals(Soil.class)) {
                tile.setGround(RandomUtils.pickRandom(PLANTABLE_CROPS).get());
                grown++;
            }
        }
        return activator + " channels a Verdant Rune, growing " + grown + " crop(s) from bare soil.";
    }
}
