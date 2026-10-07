package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.statistics.FarmerStatistics;
import java.util.List;

/**
 * The player character: a farmer who tends crops on the farm and trades with the Merchant.
 */
public class Farmer extends Actor {

    /** The Farmer's starting (and maximum) hit points. */
    private static final int STARTING_HP = 100;
    /** The Farmer's starting (and maximum) stamina. */
    private static final int STARTING_STAMINA = 100;
    /** The number of coins the Farmer starts the game with. */
    private static final int STARTING_COINS = 10000;

    /**
     * Constructor.
     *
     * @param name the farmer's name
     */
    public Farmer(String name) {
        super("Farmer " + name, '@', STARTING_HP);
        this.addNewStatistic(ActorStatistics.STAMINA, new BaseStatistic(STARTING_STAMINA));
        this.addNewStatistic(FarmerStatistics.COINS, new BaseStatistic(STARTING_COINS));
    }

    /**
     * Selects the action to perform on this turn: falls unconscious if out of
     * health, continues a multi-turn action if one is in progress, or
     * otherwise presents the player with a menu of the allowable actions.
     *
     * @see Actor#playTurn(ActionList, Action, GameMap, Display)
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction the Action this Actor took last turn
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return the Action to be performed
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        // Handle multi-turn Actions
        if (lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }

        printStatus(display);

        // return/print the console menu
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }

    /**
     * Prints this farmer's current stamina, coin balance, and what they're carrying.
     *
     * @param display the I/O object to which the status is printed
     */
    private void printStatus(Display display) {
        display.println(this + " has " + this.getStatistic(ActorStatistics.STAMINA)
                + "/" + this.getMaximumStatistic(ActorStatistics.STAMINA) + " stamina.");
        display.println(this + " has " + this.getStatistic(FarmerStatistics.COINS)
                + "/" + this.getMaximumStatistic(FarmerStatistics.COINS) + " coins.");

        List<Item> items = this.getInventory().getItems();
        if (items.isEmpty()) {
            display.println(this + " is carrying nothing.");
        } else {
            display.println(this + " is carrying: " + items);
        }
    }
}