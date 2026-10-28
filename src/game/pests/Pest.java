package game.pests;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.WanderingActor;
import game.behaviours.SeekPlantBehaviour;
import game.behaviours.WanderBehaviour;
import game.plants.Regressible;

/**
 * A creature that moves towards plants and feeds on them.
 * <p>
 * Each turn a pest standing on a plant feeds on it through an
 * {@link InfestAction}; otherwise its {@link SeekPlantBehaviour} moves it
 * towards the nearest plant. The turn is written once here. A subclass only
 * gives its landing offsets (how it moves) and its {@link #feedOn} (what
 * feeding does).
 */
public abstract class Pest extends WanderingActor implements Infestation {

    /**
     * Creates a pest.
     *
     * @param name the name of the pest
     * @param displayChar the character shown on the map
     * @param hitPoints the pest's starting hit points
     * @param landingOffsets the tiles it can land on in one move, as
     *                       {dx, dy} pairs
     */
    protected Pest(String name, char displayChar, int hitPoints, int[][] landingOffsets) {
        super(name, displayChar, hitPoints,
                new SeekPlantBehaviour(landingOffsets, new WanderBehaviour()));
    }

    /**
     * Feeds if the pest stands on a plant; otherwise moves towards one.
     *
     * @param actions collection of possible Actions for this pest
     * @param lastAction the Action this pest took last turn
     * @param map the map containing the pest
     * @param display the I/O object to which messages may be written
     * @return the Action to be performed
     */
    @Override
    protected Action playConsciousTurn(ActionList actions, Action lastAction,
                                       GameMap map, Display display) {
        if (map.locationOf(this).getGroundAs(Regressible.class) != null) {
            return new InfestAction(this);
        }
        return super.playConsciousTurn(actions, lastAction, map, display);
    }
}
