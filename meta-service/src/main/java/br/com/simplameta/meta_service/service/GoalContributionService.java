package br.com.simplameta.meta_service.service;

import br.com.simplameta.meta_service.dto.request.GoalContributionRequest;
import br.com.simplameta.meta_service.dto.response.FinancialGoalResponse;
import br.com.simplameta.meta_service.dto.response.GoalContributionResponse;
import br.com.simplameta.meta_service.exception.GoalContributionNotFoundException;
import br.com.simplameta.meta_service.model.FinancialGoal;
import br.com.simplameta.meta_service.model.GoalContribution;
import br.com.simplameta.meta_service.repository.GoalContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalContributionService {

    private final GoalContributionRepository contributionRepository;
    private final FinancialGoalService goalService;
    private final GoalProjectionService projectionService;
    private final FinanceTransactionService financeTransactionService;

    @Transactional(readOnly = true)
    public List<GoalContributionResponse> findAll(UUID userId, UUID goalId) {
        goalService.findUserGoal(userId, goalId);

        return contributionRepository
                .findByGoalIdAndUserId(goalId, userId, Sort.by(
                        Sort.Order.desc("contributionDate"),
                        Sort.Order.desc("createdAt")
                ))
                .stream()
                .map(GoalContributionResponse::from)
                .toList();
    }

    @Transactional
    public FinancialGoalResponse create(UUID userId, UUID goalId, String accessToken, GoalContributionRequest request) {
        FinancialGoal goal = goalService.findUserGoal(userId, goalId);
        UUID financeTransactionId = financeTransactionService.createGoalContributionExpense(
                accessToken,
                goal,
                request.amount(),
                request.contributionDate()
        );

        GoalContribution contribution = GoalContribution.create(
                goalId,
                userId,
                request.amount(),
                request.description(),
                true,
                financeTransactionId,
                request.contributionDate()
        );

        contributionRepository.save(contribution);

        return projectionService.toResponse(goal, userId);
    }

    @Transactional
    public void delete(UUID userId, UUID goalId, UUID contributionId, String accessToken) {
        goalService.findUserGoal(userId, goalId);

        if (!contributionRepository.existsByIdAndGoalIdAndUserId(contributionId, goalId, userId)) {
            throw new GoalContributionNotFoundException(contributionId);
        }

        GoalContribution contribution = contributionRepository
                .findById(contributionId)
                .orElseThrow(() -> new GoalContributionNotFoundException(contributionId));

        if (contribution.isAffectBalance() && contribution.getFinanceTransactionId() != null) {
            financeTransactionService.deleteGoalContributionExpense(
                    accessToken,
                    contribution.getFinanceTransactionId()
            );
        }

        contributionRepository.delete(contribution);
    }
}
