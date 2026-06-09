package com.example.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados de entrada para criacao ou atualizacao de livro")
public record LivroRequest(
        @NotBlank(message = "Titulo e obrigatorio")
        @Size(max = 120, message = "Titulo deve ter no maximo 120 caracteres")
        @Schema(example = "Dom Casmurro")
        String titulo,

        @NotBlank(message = "ISBN e obrigatorio")
        @Pattern(
                regexp = "^(97(8|9))?\\d{9}(\\d|X)$",
                message = "ISBN deve ter 10 ou 13 caracteres numericos, podendo terminar com X")
        @Schema(example = "9788535910663")
        String isbn,

        @NotNull(message = "Ano de publicacao e obrigatorio")
        @Min(value = 1400, message = "Ano de publicacao deve ser maior ou igual a 1400")
        @Max(value = 2100, message = "Ano de publicacao deve ser menor ou igual a 2100")
        @Schema(example = "1899")
        Integer anoPublicacao,

        @NotBlank(message = "Genero e obrigatorio")
        @Size(max = 60, message = "Genero deve ter no maximo 60 caracteres")
        @Schema(example = "Romance")
        String genero,

        @NotBlank(message = "Nome do autor e obrigatorio")
        @Size(max = 100, message = "Nome do autor deve ter no maximo 100 caracteres")
        @Schema(example = "Machado de Assis")
        String autorNome,

        @NotBlank(message = "Nacionalidade do autor e obrigatoria")
        @Size(max = 60, message = "Nacionalidade deve ter no maximo 60 caracteres")
        @Schema(example = "Brasileira")
        String autorNacionalidade
) {
}
