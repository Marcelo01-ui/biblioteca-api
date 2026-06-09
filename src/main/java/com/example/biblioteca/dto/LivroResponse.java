package com.example.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados pela API para um livro")
public record LivroResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Dom Casmurro") String titulo,
        @Schema(example = "9788535910663") String isbn,
        @Schema(example = "1899") Integer anoPublicacao,
        @Schema(example = "Romance") String genero,
        AutorResumoResponse autor
) {
}
