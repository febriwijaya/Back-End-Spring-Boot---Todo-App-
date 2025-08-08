package com.myproject.todo_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

    // Helper method untuk membuat body response seragam
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            String message, HttpStatus status) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        body.put("status", status.value());

        return new ResponseEntity<>(body, status);
    }

    // Handle TodoAPIException
    @ExceptionHandler(TodoAPIException.class)
    public ResponseEntity<Map<String, Object>> handleTodoAPIException(
            TodoAPIException exception, WebRequest webRequest) {

        return buildErrorResponse(exception.getMessage(), exception.getStatus());
    }


    // Handle validasi @Valid yang gagal
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> filteredErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();

            // Jika field belum ada di map, simpan
            if (!filteredErrors.containsKey(field)) {
                filteredErrors.put(field, message);
            } else {
                // Prioritaskan pesan dari @NotBlank
                if (message.toLowerCase().contains("cannot be empty") ||
                        message.toLowerCase().contains("tidak boleh kosong")) {
                    filteredErrors.put(field, message);
                }
            }
        });

        // Gabungkan semua pesan error menjadi string
        String errorMessage = String.join(", ", filteredErrors.values());

        return buildErrorResponse(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException exception, WebRequest request) {

        return buildErrorResponse("You don't have permission!", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            AuthenticationException exception, WebRequest request) {

        return buildErrorResponse("You are not authorized. Please log in first.", HttpStatus.UNAUTHORIZED);
    }

    // Handle semua exception umum
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception exception, WebRequest request) {

        return buildErrorResponse("There is an error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}