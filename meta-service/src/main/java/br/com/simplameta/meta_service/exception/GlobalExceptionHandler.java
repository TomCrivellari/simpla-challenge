package br.com.simplameta.meta_service.exception;

import br.com.simplameta.meta_service.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticatedUserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticatedUserNotFound(
            AuthenticatedUserNotFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(FinancialGoalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFinancialGoalNotFound(
            FinancialGoalNotFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(GoalContributionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGoalContributionNotFound(
            GoalContributionNotFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(FinanceServiceCommunicationException.class)
    public ResponseEntity<ErrorResponse> handleFinanceServiceCommunication(
            FinanceServiceCommunicationException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.of(
                        HttpStatus.BAD_GATEWAY.value(),
                        HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(
            InsufficientBalanceException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.of(
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> fields = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                fields.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.withFields(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Validation failed",
                        fields
                ));
    }
}
