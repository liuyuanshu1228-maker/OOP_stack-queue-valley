package game.pests;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.WanderingActor;
import game.behaviours.SeekPlantBehaviour;
import game.behaviours.WanderBehaviour;
import game.plants.Regressible;
import game.positions.area.AreaFactory;
import game.random.RandomUtils;

/**
 * A creature that moves towards plants and feeds on them.
 * <p>
 * Each turn a pest that can feed where it is feeds through an
 * {@link InfestAction}; otherwise its {@link SeekPlantBehaviour} moves it
 * towards the nearest plant. The turn is written once here. A subclass only
 * gives its landing offsets (how it moves) and its {@link #feedOn} (what
 * feeding does). It may also override {@link #canFeedAt} if it feeds on more
 * than the plant under it.
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
     * @param randomUtils the randomness used to build this pest's fallback
     *                    wandering behaviour
     * @param areaFactory builds the Area its {@link SeekPlantBehaviour} searches for plants and a landing tile
     */
    protected Pest(String name, char displayChar, int hitPoints, int[][] landingOffsets,
                   RandomUtils randomUtils, AreaFactory areaFactory) {
        super(name, displayChar, hitPoints,
                new SeekPlantBehaviour(landingOffsets, new WanderBehaviour(randomUtils), areaFactory));
    }

    /**
     * Feeds if the pest can feed where it stands; otherwise moves towards a
     * plant.
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
        if (canFeedAt(map.locationOf(this))) {
            return new InfestAction(this);
        }
        return super.playConsciousTurn(actions, lastAction, map, display);
    }

    /**
     * Decides whether this pest can feed where it stands. By default a pest
     * feeds only on the plant under it.
     *
     * @param here the tile this pest stands on
     * @return true if there is something for this pest to feed on
     */
    protected boolean canFeedAt(Location here) {
        return here.getGroundAs(Regressible.class) != null;
    }
}
