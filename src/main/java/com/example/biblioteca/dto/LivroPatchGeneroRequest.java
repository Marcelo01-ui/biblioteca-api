package com.example.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualizacao parcial do genero do livro")
public record LivroPatchGeneroRequest(
        @NotBlank(message = "Genero e obrigatorio")
        @Size(max = 60, message = "Genero deve ter no maximo 60 caracteres")
        @Schema(example = "Realismo")
        String genero
) {
}
