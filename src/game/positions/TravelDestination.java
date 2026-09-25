package game.positions;

import edu.monash.fit2099.engine.positions.Location;

/**
 * A place an Actor can travel to: a fixed Location, possibly on another map,
 * together with the name shown to the player in the menu.
 *
 * @param name the name shown to the player, e.g. "the mines"
 * @param location the fixed Location the destination refers to
 */
public record TravelDestination(String name, Location location) {
}
