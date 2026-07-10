package br.com.simplameta.meta_service.repository;

import br.com.simplameta.meta_service.model.GoalContribution;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface GoalContributionRepository extends JpaRepository<GoalContribution, UUID> {

    List<GoalContribution> findByGoalIdAndUserId(UUID goalId, UUID userId, Sort sort);

    boolean existsByIdAndGoalIdAndUserId(UUID id, UUID goalId, UUID userId);

    void deleteByIdAndGoalIdAndUserId(UUID id, UUID goalId, UUID userId);

    @Modifying
    void deleteByGoalIdAndUserId(UUID goalId, UUID userId);

    @Query("""
            select coalesce(sum(c.amount), 0)
            from GoalContribution c
            where c.goalId = :goalId
            and c.userId = :userId
            """)
    BigDecimal sumAmountByGoalIdAndUserId(
            @Param("goalId") UUID goalId,
            @Param("userId") UUID userId
    );
}
