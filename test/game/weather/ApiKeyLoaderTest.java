package game.weather;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests where the API key is found. No test touches the real .env file.
 */
class ApiKeyLoaderTest {

    private static final String NAME = "OPENWEATHER_API_KEY";

    @TempDir
    Path folder;

    @Test
    void anEnvironmentVariableIsUsedFirst() throws IOException {
        Path envFile = writeEnvFile(NAME + "=from-file");
        ApiKeyLoader loader = new ApiKeyLoader(NAME, Map.of(NAME, "from-environment"), envFile);

        assertEquals(Optional.of("from-environment"), loader.load());
    }

    @Test
    void theEnvFileIsUsedWhenTheEnvironmentHasNoKey() throws IOException {
        Path envFile = writeEnvFile("# my keys", "", "OTHER_KEY=abc", "  " + NAME + "=from-file  ");
        ApiKeyLoader loader = new ApiKeyLoader(NAME, Map.of(), envFile);

        assertEquals(Optional.of("from-file"), loader.load());
    }

    @Test
    void aBlankEnvironmentVariableFallsBackToTheEnvFile() throws IOException {
        Path envFile = writeEnvFile(NAME + "=from-file");
        ApiKeyLoader loader = new ApiKeyLoader(NAME, Map.of(NAME, "   "), envFile);

        assertEquals(Optional.of("from-file"), loader.load());
    }

    @Test
    void aCommentedOutKeyIsIgnored() throws IOException {
        Path envFile = writeEnvFile("#" + NAME + "=old-key");
        ApiKeyLoader loader = new ApiKeyLoader(NAME, Map.of(), envFile);

        assertTrue(loader.load().isEmpty());
    }

    @Test
    void noKeyAnywhereGivesNothing() {
        ApiKeyLoader loader = new ApiKeyLoader(NAME, Map.of(), folder.resolve("missing.env"));

        assertTrue(loader.load().isEmpty());
    }

    @Test
    void anEmptyValueInTheFileGivesNothing() throws IOException {
        Path envFile = writeEnvFile(NAME + "=");
        ApiKeyLoader loader = new ApiKeyLoader(NAME, Map.of(), envFile);

        assertTrue(loader.load().isEmpty());
    }

    /**
     * Writes a .env file into the temporary folder.
     *
     * @param lines the lines of the file
     * @return the file's path
     * @throws IOException if the file cannot be written
     */
    private Path writeEnvFile(String... lines) throws IOException {
        Path file = Files.createTempFile(folder, "test", ".env");
        Files.write(file, List.of(lines));
        return file;
    }
}
