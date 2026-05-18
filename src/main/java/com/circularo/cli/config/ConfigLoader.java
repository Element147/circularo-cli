package com.circularo.cli.config;

import com.circularo.cli.api.CircularoException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Resolves Circularo API credentials using a secure, multi-layered fallback strategy.
 * Now supports local `.env` resolution next to the running application binary.
 *
 * <p>Resolution order:
 * <ol>
 *   <li><b>Local env file</b> {@code .env} next to execution binary or in CWD</li>
 *   <li><b>Environment variable</b> {@code CIRCULARO_API_TOKEN} – preferred for CI/CD pipelines.</li>
 *   <li><b>Local properties file</b> {@code ~/.circularo/config.properties}, key {@code api.token}
 *       – preferred for local developer setup.</li>
 * </ol>
 */
@Component
public class ConfigLoader {

    private static final String ENV_KEY = "CIRCULARO_API_TOKEN";
    private static final String ENV_URL_KEY = "CIRCULARO_API_URL";
    private static final String CONFIG_DIR = ".circularo";
    private static final String CONFIG_FILE = "config.properties";
    private static final String TOKEN_PROPERTY = "api.token";
    private static final String URL_PROPERTY = "api.url";
    private static final String DEFAULT_API_URL = "https://api.circularo.com";

    // Value Placeholders to ignore if they have not been configured
    private static final String TOKEN_PLACEHOLDER = "your_api_token_here";
    private static final String URL_PLACEHOLDER = "your_api_url_here";

    /**
     * Returns the API token using the documented fallback chain.
     *
     * @return a non-blank API token string
     * @throws CircularoException if no token could be resolved from any source
     */
    public String getApiToken() {
        // 1. Try local .env file (next to executable or in current working directory)
        Properties envProps = loadEnvFile();
        String fileEnvToken = envProps.getProperty(ENV_KEY);
        if (fileEnvToken != null && !fileEnvToken.isBlank() && !fileEnvToken.equals(TOKEN_PLACEHOLDER)) {
            return fileEnvToken;
        }

        // 2. Primary: environment variable
        String envToken = System.getenv(ENV_KEY);
        if (envToken != null && !envToken.isBlank()) {
            return envToken;
        }

        // 3. Fallback: ~/.circularo/config.properties
        Path configFile = Path.of(System.getProperty("user.home"), CONFIG_DIR, CONFIG_FILE);
        if (Files.exists(configFile)) {
            try (InputStream in = Files.newInputStream(configFile)) {
                Properties props = new Properties();
                props.load(in);
                String fileToken = props.getProperty(TOKEN_PROPERTY);
                if (fileToken != null && !fileToken.isBlank()) {
                    return fileToken;
                }
            } catch (IOException e) {
                throw new CircularoException(
                        "Failed to read config file at " + configFile + ": " + e.getMessage(), e);
            }
        }

        // 4. Failure
        throw new CircularoException(
                "API token not found. Please configure a '.env' file next to the executable, set the CIRCULARO_API_TOKEN environment variable, or configure ~/.circularo/config.properties");
    }

    /**
     * Returns the API base URL using the documented fallback chain.
     *
     * @return a non-blank API base URL string
     */
    public String getApiUrl() {
        // 1. Try local .env file (next to executable or in current working directory)
        Properties envProps = loadEnvFile();
        String fileEnvUrl = envProps.getProperty(ENV_URL_KEY);
        if (fileEnvUrl != null && !fileEnvUrl.isBlank() && !fileEnvUrl.equals(URL_PLACEHOLDER)) {
            return fileEnvUrl;
        }

        // 2. Primary: environment variable
        String envUrl = System.getenv(ENV_URL_KEY);
        if (envUrl != null && !envUrl.isBlank()) {
            return envUrl;
        }

        // 3. Fallback: ~/.circularo/config.properties
        Path configFile = Path.of(System.getProperty("user.home"), CONFIG_DIR, CONFIG_FILE);
        if (Files.exists(configFile)) {
            try (InputStream in = Files.newInputStream(configFile)) {
                Properties props = new Properties();
                props.load(in);
                String fileUrl = props.getProperty(URL_PROPERTY);
                if (fileUrl != null && !fileUrl.isBlank()) {
                    return fileUrl;
                }
            } catch (IOException e) {
                // Gracefully fallback to default on read error
            }
        }

        // 4. Default
        return DEFAULT_API_URL;
    }

    /**
     * Loads variables from a .env file located next to the executable or in the CWD.
     */
    private Properties loadEnvFile() {
        Properties envProps = new Properties();

        // 1. Check directory containing the executing command/binary (ProcessHandle)
        Path execDir = getExecutableDirectory();
        if (execDir != null) {
            Path envFile = execDir.resolve(".env");
            if (Files.exists(envFile)) {
                loadEnvFileIntoProperties(envFile, envProps);
                return envProps;
            }
        }

        // 2. Check the current working directory as fallback
        Path cwdFile = Path.of(".").toAbsolutePath().normalize().resolve(".env");
        if (Files.exists(cwdFile)) {
            loadEnvFileIntoProperties(cwdFile, envProps);
        }

        return envProps;
    }

    /**
     * Resolves the parent directory of the currently running command/binary.
     */
    private Path getExecutableDirectory() {
        try {
            return ProcessHandle.current().info().command()
                    .map(Path::of)
                    .map(Path::getParent)
                    .map(Path::toAbsolutePath)
                    .map(Path::normalize)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parses standard 'KEY=VALUE' lines from a .env file, skipping comments and trimming values.
     */
    private void loadEnvFileIntoProperties(Path path, Properties props) {
        try {
            for (String line : Files.readAllLines(path)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int eqIndex = trimmed.indexOf('=');
                if (eqIndex > 0) {
                    String key = trimmed.substring(0, eqIndex).trim();
                    String value = trimmed.substring(eqIndex + 1).trim();
                    // Strip quotes if values are enclosed
                    if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    } else if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    }
                    props.setProperty(key, value);
                }
            }
        } catch (IOException e) {
            // Suppress and fallback gracefully
        }
    }
}
