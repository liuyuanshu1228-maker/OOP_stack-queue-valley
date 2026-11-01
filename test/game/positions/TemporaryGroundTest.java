package game.positions;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.positions.farm.Dirt;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests the countdown and restoring shared by every TemporaryGround.
 */
class TemporaryGroundTest {

    private Location location;
    private Ground original;

    @BeforeEach
    void setUp() {
        location = TestFarm.map("...", "...", "...").at(1, 1);
        original = location.getGround();
    }

    @Test
    void putsTheOriginalGroundBackWhenTheTimeIsUp() {
        location.setGround(new CountingGround(original, 3, false));

        TestFarm.tick(location, 3);

        assertSame(original, location.getGround());
    }

    @Test
    void staysOneTurnBeforeTheTimeIsUp() {
        CountingGround temporary = new CountingGround(original, 3, false);
        location.setGround(temporary);

        TestFarm.tick(location, 2);

        assertSame(temporary, location.getGround());
    }

    @Test
    void appliesItsEffectEveryTurnItIsOnTheTile() {
        CountingGround temporary = new CountingGround(original, 3, false);
        location.setGround(temporary);

        TestFarm.tick(location, 5);

        assertEquals(3, temporary.effects, "no effect after it is gone");
    }

    @Test
    void canPutTheOriginalGroundBackEarly() {
        CountingGround temporary = new CountingGround(original, 10, true);
        location.setGround(temporary);

        TestFarm.tick(location, 1);

        assertSame(original, location.getGround());
        assertEquals(1, temporary.effects);
    }

    @Test
    void rejectsADurationBelowOneTurn() {
        try {
            new CountingGround(new Dirt(), 0, false);
            fail("Expected a duration of 0 turns to be rejected");
        } catch (IllegalArgumentException expected) {
            // The duration was rejected, as it should be.
        }
    }

    /** A temporary ground that counts its effects and can end itself early. */
    private static class CountingGround extends TemporaryGround {

        private final boolean endsAfterFirstEffect;
        private int effects = 0;

        CountingGround(Ground originalGround, int durationTurns, boolean endsAfterFirstEffect) {
            super('?', "Counting Ground", originalGround, durationTurns);
            this.endsAfterFirstEffect = endsAfterFirstEffect;
        }

        @Override
        protected void tickEffect(Location location) {
            effects++;
            if (endsAfterFirstEffect) {
                revertAt(location);
            }
        }
    }
}
