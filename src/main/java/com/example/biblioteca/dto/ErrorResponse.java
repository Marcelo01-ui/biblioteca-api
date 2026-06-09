package com.example.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Resposta padronizada de erro")
public record ErrorResponse(
        @Schema(example = "2026-06-01T20:30:00Z") Instant timestamp,
        @Schema(example = "400") int status,
        @Schema(example = "Bad Request") String error,
        @Schema(example = "Dados invalidos") String message,
        @Schema(example = "/api/livros") String path,
        @Schema(description = "Erros por campo quando houver validacao de entrada")
        Map<String, String> fields
) {
    public static ErrorResponse of(
            int status,
            String error,
            String message,
            String path,
            Map<String, String> fields) {
        return new ErrorResponse(Instant.now(), status, error, message, path, fields);
    }
}
