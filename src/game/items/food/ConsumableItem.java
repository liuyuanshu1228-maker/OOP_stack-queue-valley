package game.items.food;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.Consumable;
import game.actions.ConsumeAction;
import game.statistics.StatisticAdjuster;

/**
 * Abstract base class for portable Items that can be consumed.
 * Subclasses need only define their consume effect via Consumable;
 * the ConsumeAction menu entry is provided automatically.
 */
public abstract class ConsumableItem extends Item implements Consumable {

    /**
     * Constructor.
     *
     * @param name        the name of this Item
     * @param displayChar the character to display for this Item
     */
    public ConsumableItem(String name, char displayChar) {
        super(name, displayChar);
        this.makePortable();
    }

    /**
     * Returns the list of Actions this Item allows its owner to perform,
     * namely consuming it.
     *
     * @param owner the Actor carrying this Item
     * @param map   the map the owner is currently on
     * @return an ActionList containing a ConsumeAction for this Item
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this));
        return actions;
    }

    /**
     * Shared effect for food that heals when eaten: restores HP to the Actor,
     * capped at their maximum, and describes the actual change.
     *
     * @param actor  the Actor eating this Item
     * @param amount the HP to restore
     * @return a description such as "... eats the Parsnip and is healed for 1 HP (99 -> 100)."
     */
    protected String eatToHeal(Actor actor, int amount) {
        return actor + " eats the " + this + " and is healed for "
                + new StatisticAdjuster(actor)
                        .increaseAndDescribe(ActorStatistics.HEALTH, amount, "HP")
                + ".";
    }

    /**
     * Shared effect for food that raises maximum HP when eaten. Raising a
     * maximum also restores the current value to it, so the message reports both.
     *
     * @param actor  the Actor eating this Item
     * @param amount the amount by which maximum HP increases
     * @return a description such as "... eats the Melon. Maximum HP increases by 10
     * (100 -> 110), and current HP is restored to 110."
     */
    protected String eatToRaiseMaxHealth(Actor actor, int amount) {
        String maxHealthChange =
                new StatisticAdjuster(actor)
                        .increaseMaximumAndDescribe(ActorStatistics.HEALTH, amount);
        return actor + " eats the " + this + ". Maximum HP increases by " + maxHealthChange
                + ", and current HP is restored to "
                + actor.getStatistic(ActorStatistics.HEALTH) + ".";
    }
}
