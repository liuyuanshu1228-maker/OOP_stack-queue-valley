package game.plants;

import game.testsupport.FixedRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Shared setup for the plant tests: installs a {@link FixedRandom} and
 * captures what the plants print, then puts both back after each test.
 */
abstract class PlantTestBase {

    /** The randomness used by the game during the test. */
    protected FixedRandom random;

    /** Everything printed during the test. */
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    /** The real standard output, put back after the test. */
    private PrintStream originalOut;

    @BeforeEach
    void setUpRandomAndOutput() {
        random = FixedRandom.install();
        originalOut = System.out;
        System.setOut(new PrintStream(output, true));
    }

    @AfterEach
    void restoreRandomAndOutput() {
        System.setOut(originalOut);
        FixedRandom.uninstall();
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
