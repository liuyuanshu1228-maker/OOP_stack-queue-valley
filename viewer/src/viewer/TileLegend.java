package viewer;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps the game's console display characters to a colour and short label for
 * the viewer window.
 * <p>
 * This is a local, presentation-only lookup table: it does not read or
 * depend on any class in {@code src/game} or {@code src/edu}, it only knows
 * about the single characters that already appear in the game's console
 * output. A character with no entry here still renders (as a plain grey
 * tile showing the raw character), so the viewer never breaks when a new
 * display character is added to the game -- this table can simply be
 * extended with a new line.
 */
public final class TileLegend {

    /** Shown for any character not explicitly listed below. */
    private static final TileStyle DEFAULT_STYLE = new TileStyle(new Color(230, 230, 230), "?");

    private final Map<Character, TileStyle> styles = new HashMap<>();

    public TileLegend() {
        // Ground
        add('.', new Color(222, 184, 135), "");   // Dirt
        add(',', new Color(101, 67, 33), "");      // Soil (tilled)
        add('\'', new Color(198, 239, 206), "'");  // Strawberry Seedling
        add('v', new Color(152, 216, 170), "v");   // Strawberry Sprout
        add('S', new Color(220, 20, 60), "S");     // Strawberry (ripe)
        add('y', new Color(200, 230, 180), "y");   // Cauliflower Sprout
        add('K', new Color(245, 245, 220), "K");   // Cauliflower (ripe)
        add('t', new Color(190, 225, 170), "t");   // Melon Sprout
        add('M', new Color(46, 139, 87), "M");     // Melon (ripe)
        add('T', new Color(255, 99, 71), "T");     // Tomato (ripe)
        add('P', new Color(255, 228, 181), "P");   // Parsnip (ripe)

        // Actors
        add('@', new Color(255, 215, 0), "@");     // Farmer
        add('C', new Color(255, 255, 255), "C");   // Cow
        add('c', new Color(255, 255, 224), "c");   // Chicken
        add('$', new Color(138, 43, 226), "$");    // Merchant
        add('b', new Color(47, 47, 47), "b");      // Crow

        // Items / stations / travel
        add('^', new Color(255, 69, 0), "^");      // Fire
        add('i', new Color(255, 250, 240), "i");   // Milk
        add('*', new Color(169, 169, 169), "*");   // Stone
        add('g', new Color(105, 105, 105), "g");   // Geode
        add('d', new Color(173, 216, 230), "d");   // Diamond
        add('u', new Color(184, 115, 51), "u");    // Copper ore
        add('h', new Color(160, 82, 45), "h");     // Hoe
        add('w', new Color(160, 82, 45), "w");     // Watering Can
        add('x', new Color(160, 82, 45), "x");     // Pickaxe
        add('!', new Color(192, 192, 192), "!");   // Sword
        add('0', new Color(255, 250, 205), "0");   // Egg
        add('k', new Color(245, 245, 220), "k");   // Cauliflower item
        add('p', new Color(255, 228, 181), "p");   // Parsnip item
        add('m', new Color(46, 139, 87), "m");     // Melon item
        add('o', new Color(255, 99, 71), "o");     // Tomato item
        add('s', new Color(220, 20, 60), "s");     // Strawberry item
        add('a', new Color(0, 255, 127), "a");     // Alien device
    }

    private void add(char displayChar, Color color, String label) {
        styles.put(displayChar, new TileStyle(color, label));
    }

    /**
     * Adds or replaces the style for one display character. Lets the legend
     * be extended from outside without editing this class, e.g. once a real
     * sprite set is chosen.
     *
     * @param displayChar the character to style
     * @param style        the colour/label to show it as
     */
    public void put(char displayChar, TileStyle style) {
        styles.put(displayChar, style);
    }

    /**
     * @param displayChar a character from the game's console output
     * @return the style to draw it with, or a generic placeholder style if
     *         this character has not been added to the legend yet
     */
    public TileStyle styleFor(char displayChar) {
        return styles.getOrDefault(displayChar, DEFAULT_STYLE);
    }

    /** How one display character should be drawn: a fill colour and a short label. */
    public record TileStyle(Color color, String label) {
    }
}
