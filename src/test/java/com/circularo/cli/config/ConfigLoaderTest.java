package com.circularo.cli.config;

import com.circularo.cli.api.CircularoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {

    private ConfigLoader configLoader;
    private Path tempEnvFile;

    @BeforeEach
    void setUp() {
        configLoader = new ConfigLoader();
        tempEnvFile = Path.of(".env");
        cleanupEnvFile();
    }

    @AfterEach
    void tearDown() {
        cleanupEnvFile();
    }

    private void cleanupEnvFile() {
        try {
            Files.deleteIfExists(tempEnvFile);
        } catch (IOException e) {
            // Suppress
        }
    }

    @Test
    void getApiToken_fromLocalEnvFile_returnsTokenAndUrl() throws IOException {
        // Arrange
        Files.writeString(tempEnvFile, "CIRCULARO_API_TOKEN=test-env-token\nCIRCULARO_API_URL=https://env.circularo.com");

        // Act
        String token = configLoader.getApiToken();
        String url = configLoader.getApiUrl();

        // Assert
        assertEquals("test-env-token", token);
        assertEquals("https://env.circularo.com", url);
    }

    @Test
    void getApiToken_withPlaceholder_ignoredAndThrowsOrFallsBack() throws IOException {
        // Arrange
        Files.writeString(tempEnvFile, "CIRCULARO_API_TOKEN=your_api_token_here\nCIRCULARO_API_URL=your_api_url_here");

        // Act & Assert
        // Since token is the placeholder, it should be ignored, falling back (which throws because no home config exists)
        assertThrows(CircularoException.class, () -> configLoader.getApiToken());
        
        // URL should ignore placeholder and fall back to the default URL
        assertEquals("https://api.circularo.com", configLoader.getApiUrl());
    }

    @Test
    void getApiToken_withEnclosingQuotes_stripsQuotesCorrectly() throws IOException {
        // Arrange
        Files.writeString(tempEnvFile, "CIRCULARO_API_TOKEN=\"quoted-token\"\nCIRCULARO_API_URL='https://quoted.url'");

        // Act
        String token = configLoader.getApiToken();
        String url = configLoader.getApiUrl();

        // Assert
        assertEquals("quoted-token", token);
        assertEquals("https://quoted.url", url);
    }
}
