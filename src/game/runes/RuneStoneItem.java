package game.runes;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.items.Item;

/**
 * A rune stone the Farmer carries and can place on the ground. One instance
 * of this class is created per Rune type (see StackQueueValleyWorld); the
 * Rune, display character and name are supplied at construction rather than
 * by subclassing, because nothing about an Item's own behaviour differs
 * between rune types -- only which Rune it hands to
 * {@link PlaceRuneStoneAction} does. A subclass per Rune type would just be
 * three empty classes repeating the same constructor call (YAGNI).
 */
public class RuneStoneItem extends Item {

    private final Rune rune;

    /**
     * @param name the name of this rune stone item
     * @param displayChar the character to display for this item
     * @param rune the Rune this item will place when used
     */
    public RuneStoneItem(String name, char displayChar, Rune rune) {
        super(name, displayChar);
        this.rune = rune;
        this.makePortable();
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new PlaceRuneStoneAction(this, rune));
        return actions;
    }
}
