package game.plants;

import game.positions.area.AreaFactory;
import game.testsupport.FixedRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Shared setup for the plant tests: builds a {@link FixedRandom} and the
 * {@link AreaFactory} built on it, and captures what the plants print, then
 * puts the real output back after each test.
 */
abstract class PlantTestBase {

    /** The randomness used by the plants under test. */
    protected FixedRandom random;

    /** The AreaFactory used by the plants under test, built on {@link #random}. */
    protected AreaFactory areaFactory;

    /** Everything printed during the test. */
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    /** The real standard output, put back after the test. */
    private PrintStream originalOut;

    @BeforeEach
    void setUpRandomAndOutput() {
        random = new FixedRandom();
        areaFactory = new AreaFactory(random);
        originalOut = System.out;
        System.setOut(new PrintStream(output, true));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    /**
     * Returns what was printed since the last call, and clears it.
     *
     * @return the printed text
     */
    protected String takeOutput() {
        String printed = output.toString();
        output.reset();
        return printed;
    }
}
