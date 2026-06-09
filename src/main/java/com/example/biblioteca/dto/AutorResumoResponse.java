package com.example.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumo do autor associado ao livro")
public record AutorResumoResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Machado de Assis") String nome,
        @Schema(example = "Brasileira") String nacionalidade
) {
}
