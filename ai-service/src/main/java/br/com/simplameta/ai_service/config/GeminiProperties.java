package br.com.simplameta.ai_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class GeminiProperties {

    private static final String GEMINI_API_KEY = "GEMINI_API_KEY";

    private final String apiKey;
    private final String apiUrl;
    private final String model;

    public GeminiProperties(
            @Value("${gemini.api-key:}") String configuredApiKey,
            @Value("${gemini.api-url}") String apiUrl,
            @Value("${gemini.model}") String model
    ) {
        this.apiKey = resolveApiKey(configuredApiKey);
        this.apiUrl = apiUrl.replaceAll("/+$", "");
        this.model = model;
    }

    public String apiKey() {
        return apiKey;
    }

    public String generateContentUrl() {
        return apiUrl + "/" + model + ":generateContent";
    }

    private String resolveApiKey(String configuredApiKey) {
        if (hasText(configuredApiKey)) return configuredApiKey.trim();

        String environmentValue = System.getenv(GEMINI_API_KEY);
        if (hasText(environmentValue)) return environmentValue.trim();

        return findInDotEnv().trim();
    }

    private String findInDotEnv() {
        List<Path> candidates = List.of(
                Path.of(".env"),
                Path.of("ai-service", ".env"),
                Path.of("..", "ai-service", ".env")
        );

        for (Path candidate : candidates) {
            String value = readFromDotEnv(candidate);
            if (hasText(value)) return value;
        }

        return "";
    }

    private String readFromDotEnv(Path path) {
        if (!Files.isRegularFile(path)) return "";

        try {
            return Files.readAllLines(path).stream()
                    .map(String::trim)
                    .filter(line -> line.startsWith(GEMINI_API_KEY + "="))
                    .map(line -> line.substring((GEMINI_API_KEY + "=").length()).trim())
                    .map(this::stripQuotes)
                    .filter(this::hasText)
                    .findFirst()
                    .orElse("");
        } catch (IOException exception) {
            return "";
        }
    }

    private String stripQuotes(String value) {
        if ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
