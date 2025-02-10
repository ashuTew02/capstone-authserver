package com.capstone.authServer.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.capstone.authServer.dto.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ElasticsearchOperationException (custom)
     */
    @ExceptionHandler(value = ElasticsearchOperationException.class)
    public ResponseEntity<ApiResponse<?>> handleElasticSearchException(ElasticsearchOperationException ex) {
        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Handle KafkaPublishException (custom)
     */
    @ExceptionHandler(value = KafkaPublishException.class)
    public ResponseEntity<ApiResponse<?>> handleKafkaPublishException(KafkaPublishException ex) {
        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Handle Bean Validation failures on @Valid-annotated DTOs
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        // Collect all validation errors in "field: errorMessage" format
        String errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation Error: " + errorDetails),
            HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handle invalid request params (e.g. invalid enum values)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(),
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown");

        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message),
            HttpStatus.BAD_REQUEST
        );
    }

    // @ExceptionHandler(AccessDeniedException.class)
    // public ApiResponse<?> handleAccessDenied(AccessDeniedException ex) {
    //     return ApiResponse.error(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    // }

    /**
     * Catch-all for any other unhandled exceptions
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred"),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
