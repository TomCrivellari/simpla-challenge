package br.com.simplameta.ai_service.dto.response;

import java.util.List;

public record AiChatResponse(

        String message,
        SuggestedGoalResponse suggestedGoal,
        SavingsPlanResponse savingsPlan,
        List<String> followUpQuestions,
        List<AiActionResponse> actions

) {
}
