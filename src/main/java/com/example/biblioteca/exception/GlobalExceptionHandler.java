package com.example.biblioteca.exception;

import com.example.biblioteca.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException exception,
            HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, exception.getMessage(), request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));

        return build(HttpStatus.BAD_REQUEST, "Dados invalidos", request.getRequestURI(), fields);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getConstraintViolations()
                .forEach(violation -> fields.put(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()));

        return build(HttpStatus.BAD_REQUEST, "Parametros invalidos", request.getRequestURI(), fields);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado",
                request.getRequestURI(),
                Map.of());
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> fields) {
        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                fields);
        return ResponseEntity.status(status).body(response);
    }
}
