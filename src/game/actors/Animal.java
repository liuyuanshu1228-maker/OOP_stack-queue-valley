package game.actors;

import game.actions.Feedable;
import game.behaviours.WanderBehaviour;
import game.random.RandomUtils;

/**
 * Abstract base class for farm animals that wander randomly around the map and can be fed.
 * Subclasses need only define their heal amount and the item(s) produced when fed.
 */
public abstract class Animal extends WanderingActor implements Feedable {

    /**
     * Creates a new Animal that wanders at random.
     *
     * @param name the name of the Animal
     * @param displayChar the character that will represent the Animal in the display
     * @param hitPoints the Animal's starting hit points
     * @param randomUtils the randomness used to build this Animal's wandering behaviour
     */
    public Animal(String name, char displayChar, int hitPoints, RandomUtils randomUtils) {
        super(name, displayChar, hitPoints, new WanderBehaviour(randomUtils));
    }
}