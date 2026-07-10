package br.com.simplameta.finance_service.repository;

import br.com.simplameta.finance_service.model.Transaction;
import br.com.simplameta.finance_service.model.TransactionType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUserId(UUID userId, Sort sort);

    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);

    @Query("""
            select coalesce(sum(t.amount), 0)
            from Transaction t
            where t.userId = :userId
            and t.type = :type
            """)
    BigDecimal sumAmountByUserIdAndType(
            @Param("userId") UUID userId,
            @Param("type") TransactionType type
    );
}
