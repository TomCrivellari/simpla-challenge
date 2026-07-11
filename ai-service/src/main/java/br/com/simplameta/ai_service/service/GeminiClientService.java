package br.com.simplameta.ai_service.service;

import br.com.simplameta.ai_service.config.GeminiProperties;
import br.com.simplameta.ai_service.dto.request.ChatMessageRequest;
import br.com.simplameta.ai_service.dto.response.AiChatResponse;
import br.com.simplameta.ai_service.exception.GeminiServiceException;
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
public class GeminiClientService {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(40);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String generateContentUrl;

    public GeminiClientService(ObjectMapper objectMapper, GeminiProperties properties) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = objectMapper;
        this.apiKey = properties.apiKey();
        this.generateContentUrl = properties.generateContentUrl();
    }

    public AiChatResponse createChatResponse(
            List<ChatMessageRequest> messages,
            FinancialContextService.FinancialContext financialContext
    ) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new GeminiServiceException("GEMINI_API_KEY não está configurada");
        }

        try {
            String requestBody = objectMapper.writeValueAsString(buildRequest(messages, financialContext));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(generateContentUrl))
                    .timeout(REQUEST_TIMEOUT)
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new GeminiServiceException(geminiErrorMessage(response.statusCode(), response.body()));
            }

            return objectMapper.readValue(extractOutputText(response.body()), AiChatResponse.class);
        } catch (IOException exception) {
            throw new GeminiServiceException("Não foi possível processar a resposta do Gemini");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GeminiServiceException("A requisição ao Gemini foi interrompida");
        }
    }

    private Map<String, Object> buildRequest(
            List<ChatMessageRequest> messages,
            FinancialContextService.FinancialContext financialContext
    ) {
        List<Map<String, Object>> contents = new ArrayList<>();
        messages.forEach(message -> contents.add(Map.of(
                "role", geminiRole(message.role()),
                "parts", List.of(Map.of("text", message.content()))
        )));

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("systemInstruction", Map.of(
                "parts", List.of(Map.of("text", systemPrompt(financialContext)))
        ));
        request.put("contents", contents);
        request.put("generationConfig", Map.of(
                "responseMimeType", "application/json",
                "responseJsonSchema", responseSchema()
        ));
        return request;
    }

    private String geminiRole(String role) {
        return "assistant".equalsIgnoreCase(role) || "model".equalsIgnoreCase(role)
                ? "model"
                : "user";
    }

    private String systemPrompt(FinancialContextService.FinancialContext financialContext) {
        return """
                Você é um assistente financeiro da aplicação Simpla Meta.
                Ajude o usuário a planejar metas financeiras, decidir valor objetivo,
                data limite e a melhor divisão de economia mensal, semanal e diária.

                Regras:
                - Responda sempre em português do Brasil.
                - Não crie, edite ou exclua metas diretamente.
                - O usuário sempre deve confirmar qualquer ação.
                - Se faltarem valor objetivo, prazo ou nome da meta, faça perguntas objetivas.
                - Use o saldo, metas existentes e despesas recentes apenas como contexto.
                - Não prometa rendimento financeiro nem aconselhamento de investimento.
                - Para o plano mensal, conte os meses-calendário incluindo o mês atual e o mês do prazo.
                - Calcule a parcela mensal dividindo o valor total pela quantidade desses meses.
                - Retorne somente dados que atendam ao schema JSON solicitado.

                Contexto financeiro atual em JSON:
                """ + toJson(financialContext);
    }

    private Map<String, Object> responseSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("additionalProperties", false);
        schema.put("required", List.of(
                "message", "suggestedGoal", "savingsPlan", "followUpQuestions", "actions"
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
        return schema;
    }

    private Map<String, Object> nullableObject(
            Map<String, Object> properties,
            List<String> required
    ) {
        return Map.of(
                "type", List.of("object", "null"),
                "additionalProperties", false,
                "required", required,
                "properties", properties
        );
    }

    private Map<String, Object> arrayOfStrings() {
        return Map.of("type", "array", "items", Map.of("type", "string"));
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException exception) {
            throw new GeminiServiceException("Não foi possível serializar o contexto financeiro");
        }
    }

    private String geminiErrorMessage(int statusCode, String responseBody) {
        try {
            JsonNode message = objectMapper.readTree(responseBody).path("error").path("message");
            if (message.isTextual()) {
                return "Gemini API retornou status " + statusCode + ": " + message.asText();
            }
        } catch (IOException ignored) {
            // Usa a mensagem genérica abaixo.
        }
        return "Gemini API retornou status " + statusCode;
    }

    private String extractOutputText(String responseBody) throws IOException {
        JsonNode candidates = objectMapper.readTree(responseBody).path("candidates");
        if (candidates.isArray()) {
            for (JsonNode candidate : candidates) {
                JsonNode parts = candidate.path("content").path("parts");
                if (parts.isArray()) {
                    StringBuilder output = new StringBuilder();
                    for (JsonNode part : parts) {
                        JsonNode text = part.get("text");
                        if (text != null && text.isTextual()) output.append(text.asText());
                    }
                    if (!output.isEmpty()) return output.toString();
                }
            }
        }
        throw new GeminiServiceException("A resposta do Gemini não contém texto");
    }
}
