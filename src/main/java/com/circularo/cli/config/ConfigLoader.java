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
 *
 * <p>Resolution order:
 * <ol>
 *   <li><b>Environment variable</b> {@code CIRCULARO_API_TOKEN} – preferred for CI/CD pipelines.</li>
 *   <li><b>Local properties file</b> {@code ~/.circularo/config.properties}, key {@code api.token}
 *       – preferred for local developer setup.</li>
 * </ol>
 *
 * <p>Tokens are intentionally <em>not</em> accepted as CLI arguments to prevent leakage
 * in shell history and process listings.
 */
@Component
public class ConfigLoader {

    private static final String ENV_KEY = "CIRCULARO_API_TOKEN";
    private static final String CONFIG_DIR = ".circularo";
    private static final String CONFIG_FILE = "config.properties";
    private static final String TOKEN_PROPERTY = "api.token";

    /**
     * Returns the API token using the documented fallback chain.
     *
     * @return a non-blank API token string
     * @throws CircularoException if no token could be resolved from any source
     */
    public String getApiToken() {
        // 1. Primary: environment variable
        String envToken = System.getenv(ENV_KEY);
        if (envToken != null && !envToken.isBlank()) {
            return envToken;
        }

        // 2. Fallback: ~/.circularo/config.properties
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

        // 3. Failure
        throw new CircularoException(
                "API token not found. Please set CIRCULARO_API_TOKEN env variable or configure ~/.circularo/config.properties");
    }
}
