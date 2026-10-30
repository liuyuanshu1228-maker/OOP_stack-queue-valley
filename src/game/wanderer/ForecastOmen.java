package game.wanderer;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * What tomorrow's weather brings when the Farmer asks the Wanderer for the
 * forecast, such as frost that stops nearby plants from growing.
 * <p>
 * Each omen is made with the forecast that chose it, and uses that forecast's
 * values to decide how strong it is. The action that asks for the forecast
 * depends only on this interface, so a new omen needs no change to that action.
 */
public interface ForecastOmen {

    /**
     * Brings the omen upon the actor who asked and the farm around the Wanderer.
     *
     * @param asker the actor who asked for the forecast
     * @param here the tile the Wanderer stands on
     * @return a description of what happened, or an empty string if nothing
     * happened
     */
    String bringUpon(Actor asker, Location here);
}
