package br.com.simplameta.meta_service.exception;

import java.util.UUID;

public class GoalContributionNotFoundException extends RuntimeException {

    public GoalContributionNotFoundException(UUID contributionId) {
        super("Goal contribution not found: " + contributionId);
    }
}
