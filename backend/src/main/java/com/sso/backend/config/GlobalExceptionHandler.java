package com.sso.backend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 *
 * This catches exceptions thrown anywhere in our controllers
 * and converts them into clean JSON error responses.
 *
 * Without this, Spring would return ugly HTML error pages!
 *
 * @RestControllerAdvice — applies to all @RestController classes
 * @ExceptionHandler — specifies which exception type to catch
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors (e.g., @NotBlank, @Size violations)
     * Returns a map of field → error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("error", "Validation Failed");
        response.put("fields", fieldErrors);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle RuntimeException (our custom "not found" / "access denied" errors)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 404);
        response.put("error", ex.getMessage());
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Catch-all for any other unexpected errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 500);
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
