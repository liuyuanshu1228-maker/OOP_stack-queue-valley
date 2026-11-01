package game.pestcontrol;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import game.pests.Infestation;
import game.positions.TemporaryGround;

/**
 * Salted farm ground. Pests cannot enter it, and nothing can be planted on it
 * while the salt lasts. It turns back into the ground it covered after a few
 * turns.
 */
public class SaltGround extends TemporaryGround {

    /**
     * Covers a ground with salt, shown as '+'. It lasts 8 turns.
     *
     * @param originalGround the ground to restore when the salt is gone
     */
    public SaltGround(Ground originalGround) {
        super('+', "Salt", originalGround, 8);
    }

    /**
     * Keeps pests out; every other actor may enter.
     *
     * @param actor the actor trying to enter
     * @return false if the actor is a pest
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return actor.asCapability(Infestation.class).isEmpty();
    }
}
