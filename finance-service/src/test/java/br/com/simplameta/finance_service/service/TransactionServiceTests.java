package br.com.simplameta.finance_service.service;

import br.com.simplameta.finance_service.dto.request.TransactionRequest;
import br.com.simplameta.finance_service.exception.InsufficientBalanceException;
import br.com.simplameta.finance_service.model.Transaction;
import br.com.simplameta.finance_service.model.TransactionType;
import br.com.simplameta.finance_service.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionServiceTests {

    private TransactionRepository repository;
    private TransactionService service;
    private UUID userId;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(TransactionRepository.class);
        service = new TransactionService(repository);
        userId = UUID.randomUUID();
    }

    @Test
    void blocksExpenseWhenAvailableBalanceIsInsufficient() {
        balance("100.00", "30.00");
        TransactionRequest request = request(TransactionType.EXPENSE, "80.00");

        assertThatThrownBy(() -> service.create(userId, request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("R$ 70,00");

        verify(repository, never()).save(Mockito.any());
    }

    @Test
    void allowsExpenseEqualToAvailableBalance() {
        balance("100.00", "30.00");
        TransactionRequest request = request(TransactionType.EXPENSE, "70.00");
        when(repository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.create(userId, request);

        verify(repository).save(Mockito.any());
    }

    @Test
    void editingExpenseRestoresOldAmountBeforeValidatingNewAmount() {
        UUID transactionId = UUID.randomUUID();
        Transaction existingExpense = Transaction.create(
                userId,
                TransactionType.EXPENSE,
                "Despesa",
                new BigDecimal("60.00"),
                null,
                LocalDate.now()
        );
        balance("100.00", "60.00");
        when(repository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.of(existingExpense));

        service.update(userId, transactionId, request(TransactionType.EXPENSE, "90.00"));

        verify(repository).findByIdAndUserId(transactionId, userId);
    }

    private void balance(String incomes, String expenses) {
        when(repository.sumAmountByUserIdAndType(userId, TransactionType.INCOME))
                .thenReturn(new BigDecimal(incomes));
        when(repository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE))
                .thenReturn(new BigDecimal(expenses));
    }

    private TransactionRequest request(TransactionType type, String amount) {
        return new TransactionRequest(
                type,
                "Teste",
                new BigDecimal(amount),
                null,
                LocalDate.now()
        );
    }
}
