package br.com.simplameta.meta_service.service;

import br.com.simplameta.meta_service.dto.request.FinancialGoalRequest;
import br.com.simplameta.meta_service.dto.response.FinancialGoalResponse;
import br.com.simplameta.meta_service.exception.FinancialGoalNotFoundException;
import br.com.simplameta.meta_service.model.FinancialGoal;
import br.com.simplameta.meta_service.repository.FinancialGoalRepository;
import br.com.simplameta.meta_service.repository.GoalContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancialGoalService {

    private final FinancialGoalRepository goalRepository;
    private final GoalContributionRepository contributionRepository;
    private final GoalProjectionService projectionService;
    private final FinanceTransactionService financeTransactionService;

    @Transactional(readOnly = true)
    public List<FinancialGoalResponse> findAll(UUID userId) {
        return goalRepository
                .findByUserId(userId, Sort.by(
                        Sort.Order.asc("deadline"),
                        Sort.Order.desc("createdAt")
                ))
                .stream()
                .map(goal -> projectionService.toResponse(goal, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public FinancialGoalResponse findById(UUID userId, UUID goalId) {
        FinancialGoal goal = findUserGoal(userId, goalId);

        return projectionService.toResponse(goal, userId);
    }

    @Transactional
    public FinancialGoalResponse create(UUID userId, FinancialGoalRequest request) {
        FinancialGoal goal = FinancialGoal.create(
                userId,
                request.name(),
                request.description(),
                request.targetAmount(),
                request.deadline()
        );

        FinancialGoal savedGoal = goalRepository.save(goal);

        return projectionService.toResponse(savedGoal, userId);
    }

    @Transactional
    public FinancialGoalResponse update(UUID userId, UUID goalId, FinancialGoalRequest request) {
        FinancialGoal goal = findUserGoal(userId, goalId);

        goal.update(
                request.name(),
                request.description(),
                request.targetAmount(),
                request.deadline()
        );

        return projectionService.toResponse(goal, userId);
    }

    @Transactional
    public void delete(UUID userId, UUID goalId, String accessToken) {
        if (!goalRepository.existsByIdAndUserId(goalId, userId)) {
            throw new FinancialGoalNotFoundException(goalId);
        }

        contributionRepository
                .findByGoalIdAndUserId(goalId, userId, Sort.by(Sort.Order.desc("createdAt")))
                .stream()
                .filter(contribution -> contribution.isAffectBalance()
                        && contribution.getFinanceTransactionId() != null)
                .forEach(contribution -> financeTransactionService.deleteGoalContributionExpense(
                        accessToken,
                        contribution.getFinanceTransactionId()
                ));

        contributionRepository.deleteByGoalIdAndUserId(goalId, userId);
        goalRepository.deleteByIdAndUserId(goalId, userId);
    }

    public FinancialGoal findUserGoal(UUID userId, UUID goalId) {
        return goalRepository
                .findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new FinancialGoalNotFoundException(goalId));
    }
}
