package br.com.simplameta.meta_service.dto.response;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(

        Instant timestamp,
        int status,
        String error,
        String message,
        Map<String, String> fields

) {

    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(Instant.now(), status, error, message, null);
    }

    public static ErrorResponse withFields(
            int status,
            String error,
            String message,
            Map<String, String> fields
    ) {
        return new ErrorResponse(Instant.now(), status, error, message, fields);
    }
}
