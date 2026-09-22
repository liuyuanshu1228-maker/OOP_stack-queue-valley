package game.strawberry;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * The action of eating a {@link StrawberryItem}.
 */
class ConsumeStrawberryAction extends Action {

    private final StrawberryItem strawberry;

    /**
     * Constructor.
     *
     * @param strawberry the strawberry to be eaten
     */
    ConsumeStrawberryAction(StrawberryItem strawberry) {
        this.strawberry = strawberry;
    }

    /**
     * Eats the strawberry: a rotten one poisons the actor, a fresh one heals
     * them over time.
     *
     * @see Action#execute(Actor, GameMap)
     * @param actor the actor eating the strawberry
     * @param map the map the actor is on
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        actor.getInventory().remove(strawberry);

        if (strawberry.isRotten()) {
            actor.addStatus(new StrawberryPoisonStatus(actor));
            return actor + " eats a rotten strawberry and feels a poison take hold.";
        }

        actor.addStatus(new StrawberryHealStatus(actor));
        return actor + " eats a fresh strawberry and feels a soothing warmth.";
    }

    /**
     * Describes this action in a format suitable for displaying in the menu.
     *
     * @see Action#menuDescription(Actor)
     * @param actor the actor performing the action
     * @return a string, e.g. "Farmer Bob eats the Strawberry"
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " eats the " + strawberry;
    }
}
