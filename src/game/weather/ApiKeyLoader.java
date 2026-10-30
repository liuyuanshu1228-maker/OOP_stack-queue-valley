package game.weather;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Finds an API key without it ever being written in the code.
 * <p>
 * It first looks for an environment variable with the key's name. If there is
 * none, it looks for a line {@code NAME=value} in a local {@code .env} file,
 * which is listed in {@code .gitignore} so it is never committed. Blank lines
 * and lines starting with {@code #} in the file are ignored.
 */
public class ApiKeyLoader {

    /** The character between a name and its value in the .env file. */
    private static final String SEPARATOR = "=";

    /** The name of the key, used both as the variable name and in the file. */
    private final String keyName;

    /** The environment variables to look in first. */
    private final Map<String, String> environment;

    /** The .env file to look in when the environment has no key. */
    private final Path envFile;

    /**
     * Creates a loader for one key.
     *
     * @param keyName the name of the key, for example "OPENWEATHER_API_KEY"
     * @param environment the environment variables, usually {@code System.getenv()}
     * @param envFile the .env file, usually {@code Path.of(".env")}
     */
    public ApiKeyLoader(String keyName, Map<String, String> environment, Path envFile) {
        this.keyName = keyName;
        this.environment = environment;
        this.envFile = envFile;
    }

    /**
     * Finds the key.
     *
     * @return the key, or empty if neither the environment nor the .env file
     * has a non-blank value for it
     */
    public Optional<String> load() {
        String fromEnvironment = environment.get(keyName);
        if (fromEnvironment != null && !fromEnvironment.isBlank()) {
            return Optional.of(fromEnvironment.trim());
        }
        return loadFromFile();
    }

    /**
     * Looks for the key in the .env file.
     *
     * @return the key, or empty if the file is missing, unreadable or has no
     * non-blank value for it
     */
    private Optional<String> loadFromFile() {
        if (!Files.isReadable(envFile)) {
            return Optional.empty();
        }
        try {
            return findIn(Files.readAllLines(envFile));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Finds the line that sets the key.
     *
     * @param lines the lines of the .env file
     * @return the key's value, or empty if no line sets it to a non-blank value
     */
    private Optional<String> findIn(List<String> lines) {
        String prefix = keyName + SEPARATOR;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith(prefix)) {
                String value = trimmed.substring(prefix.length()).trim();
                if (!value.isEmpty()) {
                    return Optional.of(value);
                }
            }
        }
        return Optional.empty();
    }
}
