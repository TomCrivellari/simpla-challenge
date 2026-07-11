package br.com.simplameta.finance_service.service;

import br.com.simplameta.finance_service.dto.request.TransactionRequest;
import br.com.simplameta.finance_service.dto.response.TransactionResponse;
import br.com.simplameta.finance_service.exception.TransactionNotFoundException;
import br.com.simplameta.finance_service.exception.InsufficientBalanceException;
import br.com.simplameta.finance_service.model.Transaction;
import br.com.simplameta.finance_service.model.TransactionType;
import br.com.simplameta.finance_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<TransactionResponse> findAll(UUID userId) {
        return transactionRepository
                .findByUserId(userId, Sort.by(
                        Sort.Order.desc("transactionDate"),
                        Sort.Order.desc("createdAt")
                ))
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse findById(UUID userId, UUID transactionId) {
        Transaction transaction = findUserTransaction(userId, transactionId);

        return TransactionResponse.from(transaction);
    }

    @Transactional
    public TransactionResponse create(UUID userId, TransactionRequest request) {
        validateAvailableBalance(userId, request, null);

        Transaction transaction = Transaction.create(
                userId,
                request.type(),
                request.description(),
                request.amount(),
                request.category(),
                request.transactionDate()
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        return TransactionResponse.from(savedTransaction);
    }

    @Transactional
    public TransactionResponse update(UUID userId, UUID transactionId, TransactionRequest request) {
        Transaction transaction = findUserTransaction(userId, transactionId);
        validateAvailableBalance(userId, request, transaction);

        transaction.update(
                request.type(),
                request.description(),
                request.amount(),
                request.category(),
                request.transactionDate()
        );

        return TransactionResponse.from(transaction);
    }

    @Transactional
    public void delete(UUID userId, UUID transactionId) {
        if (!transactionRepository.existsByIdAndUserId(transactionId, userId)) {
            throw new TransactionNotFoundException(transactionId);
        }

        transactionRepository.deleteByIdAndUserId(transactionId, userId);
    }

    private Transaction findUserTransaction(UUID userId, UUID transactionId) {
        return transactionRepository
                .findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }

    private void validateAvailableBalance(
            UUID userId,
            TransactionRequest request,
            Transaction currentTransaction
    ) {
        BigDecimal balance = currentBalance(userId);

        if (currentTransaction != null) {
            balance = currentTransaction.getType() == TransactionType.INCOME
                    ? balance.subtract(currentTransaction.getAmount())
                    : balance.add(currentTransaction.getAmount());
        }

        BigDecimal resultingBalance = request.type() == TransactionType.INCOME
                ? balance.add(request.amount())
                : balance.subtract(request.amount());

        if (resultingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException(balance.max(BigDecimal.ZERO));
        }
    }

    private BigDecimal currentBalance(UUID userId) {
        BigDecimal incomes = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.INCOME);
        BigDecimal expenses = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE);
        return incomes.subtract(expenses);
    }
}
