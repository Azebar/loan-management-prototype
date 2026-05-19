package com.lhv.loans.presentation.rest;

import com.lhv.loans.application.LoanNotFoundException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(LoanNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(body(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of(
                        "field", e.getField(),
                        "message", e.getDefaultMessage() == null ? "invalid" : e.getDefaultMessage()))
                .toList();
        var body = body(HttpStatus.BAD_REQUEST, "Validation failed");
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    private static Map<String, Object> body(HttpStatus status, String message) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("timestamp", Instant.now().toString());
        m.put("status", status.value());
        m.put("error", status.getReasonPhrase());
        m.put("message", message);
        return m;
    }
}
