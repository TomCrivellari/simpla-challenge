package br.com.simplameta.ai_service.service;

import br.com.simplameta.ai_service.dto.request.AiChatRequest;
import br.com.simplameta.ai_service.dto.response.AiChatResponse;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final FinancialContextService financialContextService;
    private final OpenAiClientService openAiClientService;

    public AiChatService(
            FinancialContextService financialContextService,
            OpenAiClientService openAiClientService
    ) {
        this.financialContextService = financialContextService;
        this.openAiClientService = openAiClientService;
    }

    public AiChatResponse chat(String accessToken, AiChatRequest request) {
        var financialContext = financialContextService.getContext(accessToken);

        return openAiClientService.createChatResponse(request.messages(), financialContext);
    }
}
