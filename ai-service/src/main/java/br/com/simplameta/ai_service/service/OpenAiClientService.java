package br.com.simplameta.ai_service.service;

import br.com.simplameta.ai_service.config.OpenAiProperties;
import br.com.simplameta.ai_service.dto.request.ChatMessageRequest;
import br.com.simplameta.ai_service.dto.response.AiChatResponse;
import br.com.simplameta.ai_service.exception.OpenAiServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiClientService {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(40);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String apiUrl;
    private final String model;

    public OpenAiClientService(
            ObjectMapper objectMapper,
            OpenAiProperties openAiProperties
    ) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = objectMapper;
        this.apiKey = openAiProperties.apiKey();
        this.apiUrl = openAiProperties.apiUrl();
        this.model = openAiProperties.model();
    }

    public AiChatResponse createChatResponse(
            List<ChatMessageRequest> messages,
            FinancialContextService.FinancialContext financialContext
    ) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new OpenAiServiceException("OPENAI_API_KEY is not configured");
        }

        try {
            String requestBody = objectMapper.writeValueAsString(buildRequest(messages, financialContext));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new OpenAiServiceException(openAiErrorMessage(response.statusCode(), response.body()));
            }

            String outputText = extractOutputText(response.body());

            return objectMapper.readValue(outputText, AiChatResponse.class);
        } catch (IOException exception) {
            throw new OpenAiServiceException("Could not parse OpenAI response");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new OpenAiServiceException("OpenAI request was interrupted");
        }
    }

    private Map<String, Object> buildRequest(
            List<ChatMessageRequest> messages,
            FinancialContextService.FinancialContext financialContext
    ) {
        List<Map<String, String>> input = new ArrayList<>();
        input.add(message("user", "Contexto financeiro atual em JSON:\n" + toJson(financialContext)));

        messages.forEach(chatMessage ->
                input.add(message(chatMessage.role(), chatMessage.content()))
        );

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("model", model);
        request.put("instructions", systemPrompt());
        request.put("input", input);
        request.put("text", Map.of("format", responseFormat()));

        return request;
    }

    private String systemPrompt() {
        return """
                Voce e um assistente financeiro da aplicacao Simpla Meta.
                Ajude o usuario a planejar metas financeiras, decidir valor objetivo,
                data limite e a melhor divisao de economia mensal, semanal e diaria.

                Regras:
                - Responda sempre em portugues do Brasil.
                - Nao crie, edite ou exclua metas diretamente.
                - O usuario sempre deve confirmar qualquer acao.
                - Se faltarem valor objetivo, prazo ou nome da meta, faca perguntas objetivas.
                - Use o saldo, metas existentes e despesas recentes apenas como contexto.
                - Nao prometa rendimento financeiro nem aconselhamento de investimento.
                - Retorne apenas JSON valido no schema solicitado.
                """;
    }

    private Map<String, Object> responseFormat() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("additionalProperties", false);
        schema.put("required", List.of(
                "message",
                "suggestedGoal",
                "savingsPlan",
                "followUpQuestions",
                "actions"
        ));
        schema.put("properties", Map.of(
                "message", Map.of("type", "string"),
                "suggestedGoal", nullableObject(Map.of(
                        "name", Map.of("type", "string"),
                        "description", Map.of("type", "string"),
                        "targetAmount", Map.of("type", "number"),
                        "deadline", Map.of("type", "string", "format", "date"),
                        "monthlyRequiredSavings", Map.of("type", "number")
                ), List.of("name", "description", "targetAmount", "deadline", "monthlyRequiredSavings")),
                "savingsPlan", nullableObject(Map.of(
                        "monthlyAmount", Map.of("type", "number"),
                        "weeklyAmount", Map.of("type", "number"),
                        "dailyAmount", Map.of("type", "number"),
                        "monthsUntilDeadline", Map.of("type", "integer"),
                        "notes", arrayOfStrings()
                ), List.of("monthlyAmount", "weeklyAmount", "dailyAmount", "monthsUntilDeadline", "notes")),
                "followUpQuestions", arrayOfStrings(),
                "actions", Map.of(
                        "type", "array",
                        "items", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "required", List.of("type", "label", "payload"),
                                "properties", Map.of(
                                        "type", Map.of("type", "string"),
                                        "label", Map.of("type", "string"),
                                        "payload", nullableObject(Map.of(
                                                "name", Map.of("type", "string"),
                                                "description", Map.of("type", "string"),
                                                "targetAmount", Map.of("type", "number"),
                                                "deadline", Map.of("type", "string", "format", "date")
                                        ), List.of("name", "description", "targetAmount", "deadline"))
                                )
                        )
                )
        ));

        return Map.of(
                "type", "json_schema",
                "name", "financial_goal_chat_response",
                "strict", true,
                "schema", schema
        );
    }

    private Map<String, Object> nullableObject(
            Map<String, Object> properties,
            List<String> required
    ) {
        return Map.of(
                "anyOf", List.of(
                        Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "required", required,
                                "properties", properties
                        ),
                        Map.of("type", "null")
                )
        );
    }

    private String openAiErrorMessage(int statusCode, String responseBody) {
        try {
            JsonNode message = objectMapper
                    .readTree(responseBody)
                    .path("error")
                    .path("message");

            if (message.isTextual()) {
                return "OpenAI API returned status " + statusCode + ": " + message.asText();
            }
        } catch (IOException ignored) {
            // Fall back to a generic message below.
        }

        return "OpenAI API returned status " + statusCode;
    }

    private Map<String, Object> arrayOfStrings() {
        return Map.of(
                "type", "array",
                "items", Map.of("type", "string")
        );
    }

    private Map<String, String> message(String role, String content) {
        return Map.of(
                "role", role,
                "content", content
        );
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException exception) {
            throw new OpenAiServiceException("Could not serialize financial context");
        }
    }

    private String extractOutputText(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode outputText = root.get("output_text");

        if (outputText != null && outputText.isTextual()) {
            return outputText.asText();
        }

        JsonNode output = root.get("output");
        if (output != null && output.isArray()) {
            for (JsonNode item : output) {
                JsonNode content = item.get("content");
                if (content != null && content.isArray()) {
                    for (JsonNode contentItem : content) {
                        JsonNode text = contentItem.get("text");
                        if (text != null && text.isTextual()) {
                            return text.asText();
                        }
                    }
                }
            }
        }

        throw new OpenAiServiceException("OpenAI response did not include output text");
    }
}
